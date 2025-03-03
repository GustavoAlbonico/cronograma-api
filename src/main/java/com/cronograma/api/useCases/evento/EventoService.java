package com.cronograma.api.useCases.evento;

import com.cronograma.api.entitys.Evento;
import com.cronograma.api.entitys.Fase;
import com.cronograma.api.entitys.Usuario;
import com.cronograma.api.entitys.enums.BooleanEnum;
import com.cronograma.api.entitys.enums.EventoStatusEnum;
import com.cronograma.api.exceptions.CronogramaException;
import com.cronograma.api.exceptions.EventoException;
import com.cronograma.api.exceptions.FaseException;
import com.cronograma.api.useCases.cronograma.CronogramaService;
import com.cronograma.api.useCases.evento.domains.EventoCronogramaRequestDom;
import com.cronograma.api.useCases.evento.domains.EventoRequestDom;
import com.cronograma.api.useCases.evento.domains.EventoResponseDom;
import com.cronograma.api.useCases.evento.implement.mappers.EventoMapper;
import com.cronograma.api.useCases.evento.implement.repositorys.EventoCursoRepository;
import com.cronograma.api.useCases.evento.implement.repositorys.EventoRepository;
import com.cronograma.api.useCases.evento.implement.repositorys.EventoUsuarioRepository;
import com.cronograma.api.useCases.fase.domains.FaseRequestDom;
import com.cronograma.api.useCases.usuario.UsuarioService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EventoService {

    private final EventoRepository eventoRepository;
    private final EventoCursoRepository eventoCursoRepository;
    private final EventoUsuarioRepository eventoUsuarioRepository;

    private final EventoMapper eventoMapper;

    private final CronogramaService cronogramaService;
    private final UsuarioService usuarioService;

    @Transactional(readOnly = true)
    public List<EventoResponseDom> carregarEvento(){
        Usuario usuario = usuarioService.buscarUsuarioAutenticado();
        List<Evento> eventosEncontrados = eventoRepository.findAllByUsuarioId(usuario.getId());

        return eventosEncontrados.stream()
                .map(eventoMapper::eventoParaEventoResponseDom)
                .sorted(
                        Comparator.comparing(EventoResponseDom::getVisualizadoBooleanEnum)
                                .thenComparing(EventoResponseDom::getSiglaCurso)
                )
                .toList();
    }

    public void visualizarEvento(Long id){
        Evento eventoEncontrado = eventoRepository.findById(id)
                .orElseThrow( () -> new EventoException("Nenhum evento encontrado!"));

        eventoEncontrado.setVisualizadoBooleanEnum(BooleanEnum.SIM);
        eventoRepository.save(eventoEncontrado);
    }
    private void validarUsuarioPertenceCurso(Long cursoId, final Usuario usuario){
        if(
            usuario.getCoordenador() != null &&
            usuario.getNiveisAcesso().stream().noneMatch(nivelAcesso -> nivelAcesso.getRankingAcesso() < 2)
        ){
            if(usuario.getCoordenador().getCursos().stream().noneMatch(curso -> curso.getId().equals(cursoId))){
                throw new EventoException("Você não possui acesso a este curso!");
            }
        }
    }


    public void criarEventoCronograma(EventoCronogramaRequestDom cronograma){

        final Usuario usuario = usuarioService.buscarUsuarioAutenticado();
        validarUsuarioPertenceCurso(cronograma.getCursoId(),usuario);

        Long cronogramaId = null;
        Long eventoId = null;
        try {
            validarExisteEventoExecucao(cronograma,usuario.getId());
            removerEventoAntigo(cronograma,usuario.getId());

            EventoRequestDom eventoPendente = new EventoRequestDom(
                    null,
                    List.of("Gerando cronograma"),
                    "gerar cronograma",
                    EventoStatusEnum.EXECUTANDO,
                    BooleanEnum.NAO,
                    cronograma.getCursoId(),
                    usuario.getId()
            );
            Evento evento = criarEvento(eventoPendente);
            eventoId = evento.getId();
            try {
                cronogramaId = cronogramaService.gerarCronograma(cronograma);
                evento.setEventoStatusEnum(EventoStatusEnum.SUCESSO);
                evento.setVisualizadoBooleanEnum(BooleanEnum.NAO);
                evento.setMensagens(List.of("Cronograma gerado com sucesso!"));
            } catch (CronogramaException cronogramaException) {
                evento.setEventoStatusEnum(EventoStatusEnum.ERRO);
                evento.setVisualizadoBooleanEnum(BooleanEnum.NAO);
                evento.setMensagens(cronogramaException.getMessages());
            } catch (Exception exception) {
                evento.setEventoStatusEnum(EventoStatusEnum.ERRO);
                evento.setVisualizadoBooleanEnum(BooleanEnum.NAO);
                evento.setMensagens(List.of("Erro não mapeado!"));
            }
            atualizarEvento(evento);
        } catch (EventoException eventoException){
            eventoRollBack(cronogramaId,eventoId);
            throw eventoException;
        } catch (Exception exception){
            eventoRollBack(cronogramaId,eventoId);
            throw exception;
        }
    }

    private void eventoRollBack(Long cronogramaId, Long eventoId){
        if(cronogramaId != null){
            cronogramaService.excluirCronograma(cronogramaId);
        }
        if(eventoId != null){
            eventoRepository.deleteById(eventoId);
        }
    }

    public Evento criarEvento(EventoRequestDom eventoRequestDom){
        final Evento evento = new Evento();
        eventoMapper.eventoRequestDomParaEvento(eventoRequestDom,evento,eventoCursoRepository,eventoUsuarioRepository);
        return eventoRepository.save(evento);
    }

    private void validarExisteEventoExecucao(EventoCronogramaRequestDom cronograma,Long usuarioId){
        List<Evento> eventos = eventoRepository.findAll();
        for (Evento evento: eventos){
            if (
                evento.getUsuario().getId().equals(usuarioId) &&
                evento.getCurso().getId().equals(cronograma.getCursoId()) &&
                evento.getEventoStatusEnum().equals(EventoStatusEnum.EXECUTANDO)
            ){
                throw new EventoException("Já existe um evento em execução!");
            }
        }
    }

    private void removerEventoAntigo(EventoCronogramaRequestDom cronograma,Long usuarioId){
        List<Evento> eventos = eventoRepository.findAll();
        for (Evento evento: eventos){
            if (
                evento.getUsuario().getId().equals(usuarioId) &&
                evento.getCurso().getId().equals(cronograma.getCursoId()) &&
                !evento.getEventoStatusEnum().equals(EventoStatusEnum.EXECUTANDO)
            ){
                eventoRepository.delete(evento);
            }
        }
        eventoRepository.flush();
    }

    public void atualizarEvento(Evento evento){
        eventoRepository.save(evento);
    }
}
