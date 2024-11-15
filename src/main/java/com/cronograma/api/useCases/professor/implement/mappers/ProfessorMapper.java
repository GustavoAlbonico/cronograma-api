package com.cronograma.api.useCases.professor.implement.mappers;

import com.cronograma.api.entitys.Coordenador;
import com.cronograma.api.entitys.DiaSemanaDisponivel;
import com.cronograma.api.entitys.Professor;
import com.cronograma.api.entitys.Usuario;
import com.cronograma.api.entitys.enums.StatusEnum;
import com.cronograma.api.useCases.professor.domains.*;
import com.cronograma.api.utils.regex.RegexUtil;
import org.mapstruct.*;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface ProfessorMapper {
    @Mapping(source = "cpf", target = "cpf", qualifiedByName = "cpfRetornarNumeros")
    ProfessorUsuarioRequestDom professorRequestDomParaProfessorUsuarioRequestDom(ProfessorRequestDom professorRequestDom);

    @Named("cpfRetornarNumeros")
    default String cpfRetornarNumeros(String cpf) {
        return RegexUtil.retornarNumeros(cpf);
    }

    @Mapping(target = "telefone", ignore = true)
    @Mapping(target = "usuario", ignore = true)
    Professor professorRequestDomParaProfessor(
            ProfessorRequestDom professorRequestDom,
            @Context Usuario usuario,
            @Context List<DiaSemanaDisponivel> diasSemanaDisponiveisEncontrados);

    @AfterMapping
    default void afterProfessorRequestDomParaProfessor(
            ProfessorRequestDom professorRequestDom,
            @MappingTarget Professor professor,
            @Context Usuario usuario,
            @Context List<DiaSemanaDisponivel> diasSemanaDisponiveisEncontrados
    ) {
        professor.setTelefone(RegexUtil.retornarNumeros(professorRequestDom.getTelefone()));
        professor.setUsuario(usuario);
        if (diasSemanaDisponiveisEncontrados != null){
            professor.setDiasSemanaDisponivel(new HashSet<>(diasSemanaDisponiveisEncontrados));
        } else {
            professor.setDiasSemanaDisponivel(new HashSet<>());
        }
    }

    @Mapping(source = "usuario", target = "nome", qualifiedByName = "buscarNome")
    @Mapping(source = "usuario", target = "cpf", qualifiedByName = "buscarCpf")
    @Mapping(source = "usuario", target = "email", qualifiedByName = "buscarEmail")
    @Mapping(source = "diasSemanaDisponivel", target = "diasSemanaDisponiveis")
    ProfessorResponseDom professorParaProfessorResponseDom(Professor professor);

    @Named("buscarNome")
    default String buscarNome(Usuario usuario) {
        return usuario.getNome();
    }

    @Named("buscarCpf")
    default String buscarCpf(Usuario usuario) {
        return usuario.getCpf();
    }

    @Named("buscarEmail")
    default String buscarEmail(Usuario usuario) {
        return usuario.getEmail();
    }

    @Mapping(target = "id", ignore = true)
    Professor coordenadorEncontradoParaProfessor(Coordenador coordenadorEncontrado);


    @Mapping( target = "id",ignore = true)
    @Mapping( target = "cpf",ignore = true)
    @Mapping( target = "telefone",ignore = true)
    @Mapping( target = "email",ignore = true)
    @Mapping( target = "statusEnum",ignore = true)
    @Mapping(source = "diasSemanaDisponivel", target = "diasSemanaDisponiveis")
    ProfessorResponseDom professorParaProfessorResponseDomDiasSemanaDisponiveis(Professor professor);

    @AfterMapping
    default void afterProfessorParaProfessorResponseDomDiasSemanaDisponiveis(
            @MappingTarget ProfessorResponseDom professorResponseDom
    ){
        professorResponseDom.setDiasSemanaDisponiveis(
                professorResponseDom.getDiasSemanaDisponiveis().stream()
                        .sorted(Comparator.comparing(ProfessorDiaSemanaDisponivelResponseDom::getDiaSemanaEnum))
                        .toList()
        );
    }

}