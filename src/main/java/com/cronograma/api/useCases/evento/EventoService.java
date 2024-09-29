package com.cronograma.api.useCases.evento;

import com.cronograma.api.entitys.Evento;
import com.cronograma.api.entitys.Usuario;
import com.cronograma.api.entitys.enums.EventoStatusEnum;
import com.cronograma.api.exceptions.CronogramaException;
import com.cronograma.api.exceptions.EventoException;
import com.cronograma.api.useCases.cronograma.CronogramaService;
import com.cronograma.api.useCases.evento.domains.EventoCronogramaRequestDom;
import com.cronograma.api.useCases.evento.domains.EventoRequestDom;
import com.cronograma.api.useCases.evento.implement.mappers.EventoMapper;
import com.cronograma.api.useCases.evento.implement.repositorys.EventoCursoRepository;
import com.cronograma.api.useCases.evento.implement.repositorys.EventoPeriodoRepository;
import com.cronograma.api.useCases.evento.implement.repositorys.EventoRepository;
import com.cronograma.api.useCases.evento.implement.repositorys.EventoUsuarioRepository;
import com.cronograma.api.useCases.usuario.UsuarioService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
public class EventoService {

    private final EventoRepository eventoRepository;
    private final EventoPeriodoRepository eventoPeriodoRepository;
    private final EventoCursoRepository eventoCursoRepository;
    private final EventoUsuarioRepository eventoUsuarioRepository;

    private final EventoMapper eventoMapper;

    private final CronogramaService cronogramaService;
    private final UsuarioService usuarioService;

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
    @Transactional
    public void criarEventoCronograma(EventoCronogramaRequestDom cronograma){

        final Usuario usuario = usuarioService.buscarUsuarioAutenticado();
        validarUsuarioPertenceCurso(cronograma.getCursoId(),usuario);

        Long cronogramaId = null;
        try {
            validarExisteEventoExecucao(cronograma,usuario.getId());
            removerEventoAntigo(cronograma,usuario.getId());

            EventoRequestDom eventoPendente = new EventoRequestDom(
                    null,
                    "Gerando cronograma",
                    "gerar cronograma",
                    EventoStatusEnum.EXECUTANDO,
                    cronograma.getCursoId(),
                    cronograma.getPeriodoId(),
                    usuario.getId()
            );
            Evento evento = criarEvento(eventoPendente);
            try {
                cronogramaId = cronogramaService.gerarCronograma(cronograma);
                evento.setEventoStatusEnum(EventoStatusEnum.SUCESSO);
                evento.setMensagem("Cronograma gerado com sucesso!");
            } catch (CronogramaException cronogramaException) {
                evento.setEventoStatusEnum(EventoStatusEnum.ERRO);
                evento.setMensagem(cronogramaException.getMessage());
            } catch (Exception exception) {
                evento.setEventoStatusEnum(EventoStatusEnum.ERRO);
                evento.setMensagem("Erro não mapeado!");
            }
            atualizarEvento(evento);
        } catch (EventoException eventoException){
            if(cronogramaId != null){
                cronogramaService.excluirCronograma(cronogramaId);
            }
            throw eventoException;
        } catch (Exception exception){
            if(cronogramaId != null){
                cronogramaService.excluirCronograma(cronogramaId);
            }
            throw exception;
        }
    }

    public Evento criarEvento(EventoRequestDom eventoRequestDom){
        final Evento evento = new Evento();
        eventoMapper.eventoRequestDomParaEvento(eventoRequestDom,evento,eventoCursoRepository,eventoPeriodoRepository,eventoUsuarioRepository);
        return eventoRepository.save(evento);
    }

    private void validarExisteEventoExecucao(EventoCronogramaRequestDom cronograma,Long usuarioId){
        List<Evento> eventos = eventoRepository.findAll();
        for (Evento evento: eventos){
            if (
                evento.getUsuario().getId().equals(usuarioId) &&
                evento.getCurso().getId().equals(cronograma.getCursoId()) &&
                evento.getPeriodo().getId().equals(cronograma.getPeriodoId()) &&
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
                evento.getPeriodo().getId().equals(cronograma.getPeriodoId()) &&
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
