package com.cronograma.api.useCases.professor.implement.mappers;

import com.cronograma.api.entitys.DiaSemanaDisponivel;
import com.cronograma.api.entitys.Professor;
import com.cronograma.api.useCases.professor.domains.ProfessorFormularioRequestDom;
import org.mapstruct.*;

import java.util.HashSet;
import java.util.List;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface ProfessorFormularioMapper {

    void professorFormularioRequestDomParaProfessor(
            ProfessorFormularioRequestDom professorFormularioRequestDom,
            @MappingTarget Professor professorEncontrado,
            @Context List<DiaSemanaDisponivel> diasSemanaDisponiveisEncontrados
    );

    @AfterMapping
    default void afterProfessorFormularioRequestDomParaProfessor(
            @MappingTarget Professor professorEncontrado,
            @Context List<DiaSemanaDisponivel> diasSemanaDisponiveisEncontrados
    ){
        professorEncontrado.setDiasSemanaDisponivel(new HashSet<>(diasSemanaDisponiveisEncontrados));
    }
}
