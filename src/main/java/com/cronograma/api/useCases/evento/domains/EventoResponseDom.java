package com.cronograma.api.useCases.evento.domains;

import com.cronograma.api.entitys.enums.EventoStatusEnum;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class EventoResponseDom {
    private Long id;
    private String data;
    private EventoStatusEnum eventoStatusEnum;
    private List<String> mensagens;
    private String siglaCurso;
}
