package com.cronograma.api.useCases.cronograma.implement.mappers;

import com.cronograma.api.entitys.DiaSemanaDisponivel;
import com.cronograma.api.entitys.Disciplina;
import com.cronograma.api.entitys.Professor;
import com.cronograma.api.entitys.Usuario;
import com.cronograma.api.useCases.cronograma.domains.CronogramaDisciplinaResponseDom;
import org.mapstruct.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface CronogramaDisciplinaMapper {

    default List<CronogramaDisciplinaResponseDom> listaDisciplinaParaListaCronogramaDisciplinaResponseDom(List<Disciplina> disciplinas){
        return disciplinas.stream().map(this::disciplinaParaCronogramaDisciplinaResponseDom).toList();
    }

    @Mapping(source = "professor",target = "professorNome", qualifiedByName = "buscarProfessorNome")
    CronogramaDisciplinaResponseDom disciplinaParaCronogramaDisciplinaResponseDom(Disciplina disciplina);

    @Named("buscarProfessorNome")
    default String buscarProfessorNome(Professor professor){
        return professor != null ? professor.getUsuario().getNome() : "Contratando";
    }

    default Set<Disciplina> disciplinasParaDisciplinasNovaInstancia(Set<Disciplina> disciplinasEncontradas){
        return disciplinasEncontradas.stream().map(this::disciplinaParaDisciplinaNovaInstancia).collect(Collectors.toSet());
    }

    @Mapping(target = "diasCronograma", ignore = true)
    @Mapping(target = "professor", qualifiedByName = "professorParaProfessorNovaInstancia")
    Disciplina disciplinaParaDisciplinaNovaInstancia(Disciplina disciplina);

    @Mapping(target = "disciplinas", ignore = true)
    @Mapping(target = "usuario", qualifiedByName = "usuarioParaUsuarioNovaInstancia")
    @Mapping(target = "diasSemanaDisponivel", qualifiedByName = "diasSemanaDisponivelParaDiasSemanaDisponivelNovaInstancia")
    @Named("professorParaProfessorNovaInstancia")
    Professor professorParaProfessorNovaInstancia(Professor professor);

    @Mapping(target = "niveisAcesso", ignore = true)
    @Mapping(target = "professor", ignore = true)
    @Mapping(target = "coordenador", ignore = true)
    @Mapping(target = "aluno", ignore = true)
    @Mapping(target = "eventos", ignore = true)
    @Named("usuarioParaUsuarioNovaInstancia")
    Usuario usuarioParaUsuarioNovaInstancia(Usuario usuario);

    @Named("diasSemanaDisponivelParaDiasSemanaDisponivelNovaInstancia")
    default Set<DiaSemanaDisponivel> diasSemanaDisponivelParaDiasSemanaDisponivelNovaInstancia(
            Set<DiaSemanaDisponivel> diasSemanaDisponivel) {
        return diasSemanaDisponivel.stream()
                .map(this::diaSemanaDisponivelParaDiaSemanaDisponivelNovaInstancia).collect(Collectors.toSet());
    }

    @Mapping(target = "professores", ignore = true)
    DiaSemanaDisponivel diaSemanaDisponivelParaDiaSemanaDisponivelNovaInstancia(DiaSemanaDisponivel diaSemanaDisponivel);
}
