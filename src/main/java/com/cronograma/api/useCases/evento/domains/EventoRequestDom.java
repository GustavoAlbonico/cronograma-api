package com.cronograma.api.useCases.evento.domains;
import com.cronograma.api.entitys.enums.BooleanEnum;
import com.cronograma.api.entitys.enums.EventoStatusEnum;
import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class EventoRequestDom{
    private Long id;
    private List<String> mensagens;
    private String acao;
    private EventoStatusEnum eventoStatusEnum;
    private BooleanEnum visualizadoBooleanEnum;
    private Long cursoId;
    private Long usuarioId;
}
