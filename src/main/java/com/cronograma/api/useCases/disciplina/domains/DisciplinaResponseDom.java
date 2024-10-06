package com.cronograma.api.useCases.disciplina.domains;

import com.cronograma.api.entitys.enums.BooleanEnum;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DisciplinaResponseDom {
    private Long id;
    private String nome;
    private Double cargaHoraria;
    private Double cargaHorariaDiaria;
    private String corHexadecimal;
    private BooleanEnum extensaoBooleanEnum;
    private DisciplinaCursoResponseDom curso;
    private DisciplinaFaseResponseDom fase;
    private DisciplinaProfessorResponseDom professor;
}
