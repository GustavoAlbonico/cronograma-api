package com.cronograma.api.useCases.aluno.domains;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class AlunoRequestDom {
    private String cpf;
    private String nome;
    private String email;
    private Long cursoId;
    private List<Long> faseIds;
}
