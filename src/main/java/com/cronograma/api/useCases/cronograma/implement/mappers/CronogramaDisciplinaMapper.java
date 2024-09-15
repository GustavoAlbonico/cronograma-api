package com.cronograma.api.useCases.cronograma.implement.mappers;

import com.cronograma.api.entitys.DiaSemanaDisponivel;
import com.cronograma.api.entitys.Disciplina;
import com.cronograma.api.entitys.Professor;
import org.mapstruct.*;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface CronogramaDisciplinaMapper {

    default Set<Disciplina> disciplinasParaDisciplinasNovaInstancia(Set<Disciplina> disciplinasEncontradas){
        return disciplinasEncontradas.stream().map(this::disciplinaParaDisciplinaNovaInstancia).collect(Collectors.toSet());
    }

    @Mapping(target = "diasCronograma", ignore = true)
    @Mapping(target = "professor", qualifiedByName = "professorParaProfessorNovaInstancia")
    Disciplina disciplinaParaDisciplinaNovaInstancia(Disciplina disciplina);

    @Mapping(target = "disciplinas", ignore = true)
    @Mapping(target = "usuario", ignore = true)
    @Mapping(target = "diasSemanaDisponivel", qualifiedByName = "diasSemanaDisponivelParaDiasSemanaDisponivelNovaInstancia")
    @Named("professorParaProfessorNovaInstancia")
    Professor professorParaProfessorNovaInstancia(Professor professor);

    @Named("diasSemanaDisponivelParaDiasSemanaDisponivelNovaInstancia")
    default Set<DiaSemanaDisponivel> diasSemanaDisponivelParaDiasSemanaDisponivelNovaInstancia(
            Set<DiaSemanaDisponivel> diasSemanaDisponivel) {
        return diasSemanaDisponivel.stream()
                .map(this::diaSemanaDisponivelParaDiaSemanaDisponivelNovaInstancia).collect(Collectors.toSet());
    }

    @Mapping(target = "professor", ignore = true)
    DiaSemanaDisponivel diaSemanaDisponivelParaDiaSemanaDisponivelNovaInstancia(DiaSemanaDisponivel diaSemanaDisponivel);
}
