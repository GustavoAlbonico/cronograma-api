package com.cronograma.api.useCases.aluno.domains;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AlunoCursoResponseDom {
    private Long id;
    private String sigla;
    private String nome;
}
