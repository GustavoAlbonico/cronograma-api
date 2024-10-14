package com.cronograma.api.useCases.aluno.implement.mappers;

import com.cronograma.api.entitys.Aluno;
import com.cronograma.api.entitys.Curso;
import com.cronograma.api.entitys.Fase;
import com.cronograma.api.useCases.aluno.domains.AlunoRequestDom;
import com.cronograma.api.utils.regex.RegexUtil;
import org.mapstruct.*;

import java.util.HashSet;
import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface AlunoEncontradoMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "usuario", ignore = true)
    @Mapping(target = "curso", ignore = true)
    @Mapping(target = "fases", ignore = true)
    void alunoRequestDomParaAlunoEncontrado(
            AlunoRequestDom alunoRequestDom,
            @MappingTarget Aluno aluno,
            @Context Curso cursoEncontrado,
            @Context List<Fase> fasesEncontradas
    );

    @AfterMapping
    default void afterAlunoRequestDomParaAlunoEncontrado(
            @MappingTarget Aluno aluno,
            @Context Curso cursoEncontrado,
            @Context List<Fase> fasesEncontradas
    ){
        aluno.setCurso(cursoEncontrado);
        aluno.setFases(new HashSet<>(fasesEncontradas));
    }
}
