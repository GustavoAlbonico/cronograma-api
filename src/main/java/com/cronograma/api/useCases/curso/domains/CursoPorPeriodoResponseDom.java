package com.cronograma.api.useCases.curso.domains;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Set;

@Getter
@Setter
public class CursoPorPeriodoResponseDom {
    private Long id;
    private String nome;
    private String sigla;
    private List<CursoPorPeriodoFaseResponseDom> fases;
    private String nomeNivelAcesso;
    private Integer rankingNivelAcesso;
    private boolean possuiCurso;
}
