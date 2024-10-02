package com.cronograma.api.useCases.professor.domains;

import com.cronograma.api.entitys.enums.DiaSemanaEnum;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ProfessorFormularioRequestDom {
    private List<Long> diaSemanaDisponivelIds;
}
