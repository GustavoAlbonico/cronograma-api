package com.cronograma.api.useCases.aluno.domains;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class AlunoImportarDom {
    private String cpf;
    private String nome;
    private String telefone;
    private String email;
}
