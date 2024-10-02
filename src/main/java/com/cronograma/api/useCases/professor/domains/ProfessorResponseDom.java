package com.cronograma.api.useCases.professor.domains;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ProfessorResponseDom {
    private Long id;
    private String cpf;
    private String nome;
    private String telefone;
    private String email;
    private List<ProfessorDiaSemanaDisponivelResponseDom> professorDiasSemanaDisponiveisResponseDom;
}
