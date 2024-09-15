package com.cronograma.api.useCases.evento;

import com.cronograma.api.entitys.Evento;
import com.cronograma.api.entitys.enums.EventoStatusEnum;
import com.cronograma.api.exceptions.CronogramaException;
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
            EventoRequestDom eventoPendente =  new EventoRequestDom(
                    null,
                    "Gerando cronograma",
                    "gerar cronograma",
                    EventoStatusEnum.PROCESSANDO,
                    cronograma.getCursoId(),
                    cronograma.getPeriodoId(),
                    cronograma.getUsuarioId()
            );

            Evento evento = criarEvento(eventoPendente);
            try {
                cronogramaService.gerarCronogramaPorCursos(cronograma);
                evento.setEventoStatusEnum(EventoStatusEnum.SUCESSO);
                evento.setMensagem("Evento gerado com sucesso!");
            } catch (CronogramaException cronogramaException) {
                evento.setEventoStatusEnum(EventoStatusEnum.ERRO);
                evento.setMensagem(cronogramaException.getMessage());
            } catch (Exception exception) {
                evento.setEventoStatusEnum(EventoStatusEnum.ERRO);
                evento.setMensagem("Erro não mapeado!");
            }
            atualizarEvento(evento);
    }

    public Evento criarEvento(EventoRequestDom eventoRequestDom){
        final Evento evento = new Evento();
        eventoMapper.eventoRequestDomParaEvento(eventoRequestDom,evento,eventoCursoRepository,eventoPeriodoRepository,eventoUsuarioRepository);
        return eventoRepository.save(evento);
    }

    public void atualizarEvento(Evento evento){
        eventoRepository.save(evento);
    }
}
