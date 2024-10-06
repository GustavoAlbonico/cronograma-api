package com.cronograma.api.useCases.evento.domains;

import com.cronograma.api.entitys.enums.EventoStatusEnum;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EventoResponseDom {
    private Long id;
    private String data;
    private EventoStatusEnum eventoStatusEnum;
    private String mensagem;
    private String siglaCurso;
}
