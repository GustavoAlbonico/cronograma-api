package com.cronograma.api.useCases.curso.domains;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CursoPorUsuarioResponseDom {
    private Long id;
    private String nome;
    private String sigla;
    private List<CursoPorUsuarioFaseResponseDom> fases;
}
