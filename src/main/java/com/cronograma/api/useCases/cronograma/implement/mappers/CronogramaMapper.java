package com.cronograma.api.useCases.cronograma.implement.mappers;

import com.cronograma.api.entitys.Cronograma;
import com.cronograma.api.entitys.Curso;
import com.cronograma.api.entitys.Periodo;
import com.cronograma.api.exceptions.CronogramaException;
import com.cronograma.api.useCases.cronograma.domains.CronogramaRequestDom;
import com.cronograma.api.useCases.cronograma.implement.repositorys.CronogramaCursoRepository;
import com.cronograma.api.useCases.cronograma.implement.repositorys.CronogramaPeriodoRepository;
import org.mapstruct.*;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface CronogramaMapper {

    @Mapping(target = "curso", ignore = true)
    @Mapping(target = "periodo", ignore = true)
    void cronogramaRequestDomParaCronograma(
            CronogramaRequestDom cronogramaRequestDom,
            @MappingTarget Cronograma cronograma,
            @Context CronogramaCursoRepository cronogramaCursoRepository,
            @Context CronogramaPeriodoRepository cronogramaPeriodoRepository);

    @AfterMapping
    default void afterCronogramaRequestDomParaCronograma(
            CronogramaRequestDom cronogramaRequestDom,
            @MappingTarget Cronograma cronograma,
            @Context CronogramaCursoRepository cronogramaCursoRepository,
            @Context CronogramaPeriodoRepository cronogramaPeriodoRepository
    ){
        final Curso cursoEncontrado = cronogramaCursoRepository.findById(cronogramaRequestDom.getCursoId())
                .orElseThrow(() -> new CronogramaException("Nenhum curso encontrado!"));
        final Periodo periodoEncontrado = cronogramaPeriodoRepository.findById(cronogramaRequestDom.getPeriodoId())
                .orElseThrow(() -> new CronogramaException("Nenhum periodo encontrado!"));

        cronograma.setCurso(cursoEncontrado);
        cronograma.setPeriodo(periodoEncontrado);
    }
}
