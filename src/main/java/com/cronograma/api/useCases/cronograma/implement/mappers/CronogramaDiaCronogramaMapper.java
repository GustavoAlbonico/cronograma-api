package com.cronograma.api.useCases.cronograma.implement.mappers;

import com.cronograma.api.entitys.DiaCronograma;
import com.cronograma.api.entitys.DiaSemanaDisponivel;
import com.cronograma.api.entitys.Disciplina;
import com.cronograma.api.entitys.enums.DiaSemanaEnum;
import com.cronograma.api.useCases.cronograma.domains.CronogramaDiaCronogramaResponseDom;
import org.mapstruct.*;

import java.util.Arrays;
import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface CronogramaDiaCronogramaMapper {

    default List<CronogramaDiaCronogramaResponseDom> listaDiaCronogramaParaListaCronogramaDiaCronogramaResponseDom(List<DiaCronograma> diasCronograma){
        return diasCronograma.stream().map(this::diaCronogramaParaCronogramaDiaCronogramaResponseDom).toList();
    }

    @Mapping(source = "disciplina",target = "professorDiasSemanaEnum", qualifiedByName = "buscarProfessorDiasSemanaEnum")
    @Mapping(source = "disciplina",target = "professorNome", qualifiedByName = "buscarProfessorNome")
    @Mapping(source = "disciplina",target = "disciplinaNome", qualifiedByName = "buscarDisciplinaNome")
    @Mapping(source = "disciplina",target = "corHexadecimal", qualifiedByName = "buscarCorHexadecimal")
    CronogramaDiaCronogramaResponseDom diaCronogramaParaCronogramaDiaCronogramaResponseDom(DiaCronograma diaCronograma);

    @Named("buscarProfessorDiasSemanaEnum")
    default List<DiaSemanaEnum> buscarProfessorDiaSemanaDisponivel(Disciplina disciplina){
        return disciplina != null ?
                    disciplina.getProfessor() != null ?
                    disciplina.getProfessor().getDiasSemanaDisponivel().stream().map(DiaSemanaDisponivel::getDiaSemanaEnum).toList() :
                    Arrays.stream(DiaSemanaEnum.values()).filter(diaSemanaEnum -> !diaSemanaEnum.equals(DiaSemanaEnum.DOMINGO)).toList()
               : null;
    }
    @Named("buscarProfessorNome")
    default String buscarProfessorNome(Disciplina disciplina){
        return disciplina != null ? disciplina.getProfessor() != null ? disciplina.getProfessor().getUsuario().getNome() : "Contratando" : null;
    }
    @Named("buscarDisciplinaNome")
    default String buscarDisciplinaNome(Disciplina disciplina){
        return disciplina != null ? disciplina.getNome() : null;
    }
    @Named("buscarCorHexadecimal")
    default String buscarCorHexadecimal(Disciplina disciplina){
        return disciplina != null ? disciplina.getCorHexadecimal() : null;
    }
}
