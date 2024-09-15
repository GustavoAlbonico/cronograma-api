package com.cronograma.api.useCases.evento.domains;
import com.cronograma.api.entitys.enums.EventoStatusEnum;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class EventoRequestDom{

    private Long id;
    private String mensagem;
    private String acao;
    private EventoStatusEnum eventoStatusEnum;
    private Long cursoId;
    private Long periodoId;
    private Long usuarioId;
}
