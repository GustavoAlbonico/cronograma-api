package com.cronograma.api.useCases.periodo.domains;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class PeriodoRequestDom {
    private String nome;
    private LocalDate dataInicial;
    private LocalDate dataFinal;
}
