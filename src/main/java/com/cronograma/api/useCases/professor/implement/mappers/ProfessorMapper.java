package com.cronograma.api.useCases.professor.implement.mappers;

import com.cronograma.api.entitys.Professor;
import com.cronograma.api.entitys.Usuario;
import com.cronograma.api.useCases.professor.domains.ProfessorRequestDom;
import com.cronograma.api.useCases.professor.domains.ProfessorResponseDom;
import com.cronograma.api.useCases.professor.domains.ProfessorUsuarioRequestDom;
import com.cronograma.api.utils.regex.RegexUtil;
import org.mapstruct.*;
@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface ProfessorMapper {
    @Mapping(source = "cpf", target = "cpf", qualifiedByName = "cpfRetornarNumeros")
    ProfessorUsuarioRequestDom professorRequestDomParaProfessorUsuarioRequestDom(ProfessorRequestDom professorRequestDom);

    @Named("cpfRetornarNumeros")
    default String cpfRetornarNumeros(String cpf){
        return RegexUtil.retornarNumeros(cpf);
    }

    @Mapping(target = "telefone", ignore = true)
    @Mapping(target = "usuario", ignore = true)
    Professor professorRequestDomParaProfessor(
            ProfessorRequestDom professorRequestDom,
            @Context Usuario usuario);

    @AfterMapping
    default void afterProfessorRequestDomParaProfessor(
            ProfessorRequestDom professorRequestDom,
            @MappingTarget Professor professor,
            @Context Usuario usuario
    ){
        professor.setTelefone(RegexUtil.retornarNumeros(professorRequestDom.getTelefone()));
        professor.setUsuario(usuario);
    }

    @Mapping(source = "usuario",target = "nome", qualifiedByName = "buscarNome")
    @Mapping(source = "usuario",target = "cpf", qualifiedByName = "buscarCpf")
    @Mapping(source = "usuario",target = "email", qualifiedByName = "buscarEmail")
    ProfessorResponseDom professorParaProfessorResponseDom(Professor professor);

    @Named("buscarNome")
    default String buscarNome(Usuario usuario){
        return usuario.getNome();
    }

    @Named("buscarCpf")
    default String buscarCpf(Usuario usuario){
        return usuario.getCpf();
    }

    @Named("buscarEmail")
    default String buscarEmail(Usuario usuario){
        return usuario.getEmail();
    }

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "telefone",qualifiedByName = "editarTelefone")
    @Mapping(target = "usuario", ignore = true)
    @Mapping(target = "disciplinas", ignore = true)
    void professorRequestDomParaProfessorEncontrado(
            ProfessorRequestDom professorRequestDom,
            @MappingTarget Professor professor
    );

    @Named("editarTelefone")
    default String editarTelefone (String telefone){
        return RegexUtil.retornarNumeros(telefone);
    }
}
