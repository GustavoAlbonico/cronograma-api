package com.cronograma.api.useCases.aluno.domains;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class AlunoResponseDom {
    private Long id;
    private String cpf;
    private String nome;
    private String telefone;
    private String email;
    private AlunoCursoResponseDom curso;
    private List<AlunoFaseResponseDom> fases;
}
