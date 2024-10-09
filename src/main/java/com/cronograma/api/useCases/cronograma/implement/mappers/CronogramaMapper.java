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
            @Context Periodo periodo,
            @Context Curso curso);

    @AfterMapping
    default void afterCronogramaRequestDomParaCronograma(
            @MappingTarget Cronograma cronograma,
            @Context Periodo periodo,
            @Context Curso curso
    ){
        cronograma.setCurso(curso);
        cronograma.setPeriodo(periodo);
    }
}
