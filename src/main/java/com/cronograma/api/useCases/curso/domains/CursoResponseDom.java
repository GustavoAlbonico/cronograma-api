package com.cronograma.api.useCases.curso.domains;

import com.cronograma.api.entitys.enums.StatusEnum;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CursoResponseDom {
    private Long id;
    private String nome;
    private String sigla;
    private StatusEnum statusEnum;
    private CursoCoordenadorResponseDom coordenador;
    private List<CursoFaseResponseDom> fases;
}
