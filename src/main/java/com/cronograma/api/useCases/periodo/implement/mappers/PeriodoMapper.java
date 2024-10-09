package com.cronograma.api.useCases.periodo.implement.mappers;

import com.cronograma.api.entitys.Periodo;
import com.cronograma.api.useCases.periodo.domains.PeriodoRequestDom;
import com.cronograma.api.useCases.periodo.domains.PeriodoResponseDom;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface PeriodoMapper {

    Periodo periodoRequestDomParaPeriodo(PeriodoRequestDom periodoRequestDom);

    void periodoRequestDomParaPeriodoEncontrado(
            PeriodoRequestDom periodoRequestDom,
            @MappingTarget Periodo periodoEncontrado
    );

    PeriodoResponseDom periodoParaPeriodoResponseDom(Periodo periodo);
}
