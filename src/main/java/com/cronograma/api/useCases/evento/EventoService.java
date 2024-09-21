package com.cronograma.api.useCases.evento;

import com.cronograma.api.entitys.Evento;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class EventoService {

    @Autowired
    private EventoRepository eventoRepository;

    @Autowired
    private EventoPeriodoRepository eventoPeriodoRepository;

    @Autowired
    private EventoCursoRepository eventoCursoRepository;

    @Autowired
    private EventoUsuarioRepository eventoUsuarioRepository;

    @Autowired
    private EventoMapper eventoMapper;

    final private CronogramaService cronogramaService;


    public EventoService(CronogramaService cronogramaService) {
        this.cronogramaService = cronogramaService;
    }

    @Transactional
    public void gerarCronograma(EventoCronogramaRequestDom cronograma){
        Long cronogramaId = null;
        try {
            validarExisteEventoExecucao(cronograma);
            removerEventoAntigo(cronograma);

            EventoRequestDom eventoPendente = new EventoRequestDom(
                    null,
                    "Gerando cronograma",
                    "gerar cronograma",
                    EventoStatusEnum.EXECUTANDO,
                    cronograma.getCursoId(),
                    cronograma.getPeriodoId(),
                    cronograma.getUsuarioId()
            );
            Evento evento = criarEvento(eventoPendente);
            try {
                cronogramaId = cronogramaService.gerarCronogramaPorCursos(cronograma);
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

    private void validarExisteEventoExecucao(EventoCronogramaRequestDom cronograma){
        List<Evento> eventos = eventoRepository.findAll();
        for (Evento evento: eventos){
            if (
                evento.getUsuario().getId().equals(cronograma.getUsuarioId()) &&
                evento.getCurso().getId().equals(cronograma.getCursoId()) &&
                evento.getPeriodo().getId().equals(cronograma.getPeriodoId()) &&
                evento.getEventoStatusEnum().equals(EventoStatusEnum.EXECUTANDO)
            ){
                throw new EventoException("Já existe um evento em execução!");
            }
        }
    }

    private void removerEventoAntigo(EventoCronogramaRequestDom cronograma){
        List<Evento> eventos = eventoRepository.findAll();
        for (Evento evento: eventos){
            if (
                evento.getUsuario().getId().equals(cronograma.getUsuarioId()) &&
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
