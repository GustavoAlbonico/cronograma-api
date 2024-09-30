package com.cronograma.api.useCases.professor.domains;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProfessorRequestDom {
    private String cpf;
    private String nome;
    private String senha;
    private String telefone;
    private String email;
}
