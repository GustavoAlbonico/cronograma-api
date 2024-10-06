package com.cronograma.api.useCases.disciplina.domains;

import com.cronograma.api.entitys.enums.BooleanEnum;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DisciplinaRequestDom {
    private String nome;
    private Double cargaHoraria;
    private Double cargaHorariaDiaria;
    private String corHexadecimal;
    private BooleanEnum extensaoBooleanEnum;
    private Long cursoId;
    private Long faseId;
    private Long professorId;
}
