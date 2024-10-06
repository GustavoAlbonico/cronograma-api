package com.cronograma.api.useCases.diaSemanaDisponivel.implement.mappers;


import com.cronograma.api.entitys.DiaSemanaDisponivel;
import com.cronograma.api.useCases.diaSemanaDisponivel.domains.DiaSemanaDisponivelResponseDom;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface DiaSemanaDisponivelMapper {

    DiaSemanaDisponivelResponseDom diaSemanaDisponivelParaDiaSemanaDisponivelResponseDom(DiaSemanaDisponivel diaSemanaDisponivel);
}
