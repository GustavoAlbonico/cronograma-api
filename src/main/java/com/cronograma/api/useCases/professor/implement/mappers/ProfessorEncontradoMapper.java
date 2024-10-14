package com.cronograma.api.useCases.professor.implement.mappers;

import com.cronograma.api.entitys.DiaSemanaDisponivel;
import com.cronograma.api.entitys.Professor;
import com.cronograma.api.useCases.professor.domains.ProfessorRequestDom;
import com.cronograma.api.utils.regex.RegexUtil;
import org.mapstruct.*;

import java.util.HashSet;
import java.util.List;
@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface ProfessorEncontradoMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "telefone", qualifiedByName = "editarTelefone")
    @Mapping(target = "usuario", ignore = true)
    @Mapping(target = "disciplinas", ignore = true)
    void professorRequestDomParaProfessorEncontrado(
            ProfessorRequestDom professorRequestDom,
            @MappingTarget Professor professor,
            @Context List<DiaSemanaDisponivel> diasSemanaDisponiveisEncontrados
    );

    @AfterMapping
    default void afterProfessorRequestDomParaProfessorEncontrado(
            @MappingTarget Professor professor,
            @Context List<DiaSemanaDisponivel> diasSemanaDisponiveisEncontrados
    ){
        if (diasSemanaDisponiveisEncontrados != null) {
            professor.setDiasSemanaDisponivel(new HashSet<>(diasSemanaDisponiveisEncontrados));
        } else {
            professor.setDiasSemanaDisponivel(new HashSet<>());
        }
    }

    @Named("editarTelefone")
    default String editarTelefone(String telefone) {
        return RegexUtil.retornarNumeros(telefone);
    }
}
