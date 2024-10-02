package com.cronograma.api.useCases.diaSemanaDisponivel.domains;

import com.cronograma.api.entitys.enums.DiaSemanaEnum;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DiaSemanaDisponivelResponseDom {
    private Long id;
    private DiaSemanaEnum diaSemanaEnum;
}
