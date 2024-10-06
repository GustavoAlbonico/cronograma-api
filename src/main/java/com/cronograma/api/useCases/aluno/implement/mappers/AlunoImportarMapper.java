package com.cronograma.api.useCases.aluno.implement.mappers;

import com.cronograma.api.entitys.Aluno;
import com.cronograma.api.entitys.Curso;
import com.cronograma.api.entitys.Fase;
import com.cronograma.api.entitys.Usuario;
import com.cronograma.api.useCases.aluno.domains.AlunoImportarDom;
import com.cronograma.api.useCases.aluno.domains.AlunoUsuarioRequestDom;
import com.cronograma.api.utils.regex.RegexUtil;
import org.apache.commons.csv.CSVRecord;
import org.mapstruct.*;

import java.util.HashSet;
import java.util.Set;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface AlunoImportarMapper {


    default AlunoImportarDom csvRecordParaAlunoImportarDom(CSVRecord linha){
        return new AlunoImportarDom(
                linha.get("cpf"),
                linha.get("nome"),
                linha.get("telefone"),
                linha.get("email")
        );
    }


    @Mapping(source = "cpf", target = "cpf", qualifiedByName = "cpfRetornarNumeros")
    AlunoUsuarioRequestDom alunoImportarDomParaAlunoUsuarioRequestDom(AlunoImportarDom alunoImportarDom);

    @Named("cpfRetornarNumeros")
    default String cpfRetornarNumeros(String cpf){
        return RegexUtil.retornarNumeros(cpf);
    }

    @Mapping(target = "telefone", ignore = true)
    @Mapping(target = "usuario", ignore = true)
    @Mapping(target = "curso", ignore = true)
    @Mapping(target = "fases", ignore = true)
    Aluno alunoImportarDomParaAluno(
            AlunoImportarDom alunoImportarDom,
            @Context Usuario usuario,
            @Context Curso cursoEncontrado,
            @Context Fase faseEncontrada
    );

    @AfterMapping
    default void afterAlunoImportarDomParaAluno(
            AlunoImportarDom alunoImportarDom,
            @MappingTarget Aluno aluno,
            @Context Usuario usuario,
            @Context Curso cursoEncontrado,
            @Context Fase faseEncontrada
    ){
        Set<Fase> fases = new HashSet<>();
        fases.add(faseEncontrada);

        aluno.setTelefone(RegexUtil.retornarNumeros(alunoImportarDom.getTelefone()));
        aluno.setUsuario(usuario);
        aluno.setCurso(cursoEncontrado);
        aluno.setFases(fases);
    }
}
