package com.cronograma.api.useCases.periodo.domains;

import com.cronograma.api.entitys.enums.StatusEnum;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class PeriodoResponseDom {
    private Long id;
    private String nome;
    private LocalDate dataInicial;
    private LocalDate dataFinal;
    private StatusEnum statusEnum;
}
