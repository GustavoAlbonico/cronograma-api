package com.cronograma.api.useCases.aluno.implement.mappers;

import com.cronograma.api.entitys.Aluno;
import com.cronograma.api.entitys.Curso;
import com.cronograma.api.entitys.Fase;
import com.cronograma.api.entitys.Usuario;
import com.cronograma.api.useCases.aluno.domains.AlunoRequestDom;
import com.cronograma.api.useCases.aluno.domains.AlunoResponseDom;
import com.cronograma.api.useCases.aluno.domains.AlunoUsuarioRequestDom;
import com.cronograma.api.utils.regex.RegexUtil;
import org.mapstruct.*;

import java.util.HashSet;
import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface AlunoMapper {

    @Mapping(source = "cpf", target = "cpf", qualifiedByName = "cpfRetornarNumeros")
    AlunoUsuarioRequestDom alunoRequestDomParaAlunoUsuarioRequestDom(AlunoRequestDom alunoRequestDom);

    @Named("cpfRetornarNumeros")
    default String cpfRetornarNumeros(String cpf){
        return RegexUtil.retornarNumeros(cpf);
    }

    @Mapping(target = "usuario", ignore = true)
    @Mapping(target = "curso", ignore = true)
    @Mapping(target = "fases", ignore = true)
    Aluno alunoRequestDomParaAluno(
            AlunoRequestDom alunoRequestDom,
            @Context Usuario usuario,
            @Context Curso cursoEncontrado,
            @Context List<Fase> fasesEncontradas
    );

    @AfterMapping
    default void afterAlunoRequestDomParaAluno(
            @MappingTarget Aluno aluno,
            @Context Usuario usuario,
            @Context Curso cursoEncontrado,
            @Context List<Fase> fasesEncontradas
    ){
        aluno.setUsuario(usuario);
        aluno.setCurso(cursoEncontrado);
        aluno.setFases(new HashSet<>(fasesEncontradas));
    }

    @Mapping(source = "usuario",target = "nome", qualifiedByName = "buscarNome")
    @Mapping(source = "usuario",target = "cpf", qualifiedByName = "buscarCpf")
    @Mapping(source = "usuario",target = "email", qualifiedByName = "buscarEmail")
    AlunoResponseDom alunoParaAlunoResponseDom(Aluno aluno);

    @Named("buscarNome")
    default String buscarNome(Usuario usuario){
        return usuario.getNome();
    }
    @Named("buscarEmail")
    default String buscarEmail(Usuario usuario){
        return usuario.getEmail();
    }

    @Named("buscarCpf")
    default String buscarCpf(Usuario usuario){
        return usuario.getCpf();
    }

}
