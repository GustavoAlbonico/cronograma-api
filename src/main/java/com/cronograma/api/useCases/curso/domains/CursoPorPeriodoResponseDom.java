package com.cronograma.api.useCases.curso.domains;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CursoPorPeriodoResponseDom {
    private Long id;
    private String nome;
    private String sigla;
    private List<CursoPorPeriodoFaseResponseDom> fases;
    private boolean editavel;
    private boolean possuiCurso;
}
