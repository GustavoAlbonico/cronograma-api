package com.cronograma.api.useCases.curso.implement.mappers;

import com.cronograma.api.entitys.Curso;
import com.cronograma.api.entitys.Fase;
import com.cronograma.api.useCases.curso.domains.CursoPorPeriodoFaseResponseDom;
import com.cronograma.api.useCases.curso.domains.CursoPorPeriodoResponseDom;
import org.mapstruct.*;

import java.util.ArrayList;
import java.util.List;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface CursoPorPeriodoMapper {

    @Mapping(target = "fases", ignore = true)
    void cursoParaCursoPorPeriodoResponseDom(
            Curso curso,
            @MappingTarget CursoPorPeriodoResponseDom cursoPorPeriodoResponseDom,
            @Context List<CursoPorPeriodoFaseResponseDom> fasesPorPeriodo
    );

    @AfterMapping
    default void afterCursoParaCursoPorPeriodoResponseDom(
            @MappingTarget CursoPorPeriodoResponseDom cursoPorPeriodoResponseDom,
            @Context List<CursoPorPeriodoFaseResponseDom> fasesPorPeriodo
    ){
        cursoPorPeriodoResponseDom.setFases(new ArrayList<>(fasesPorPeriodo));
        cursoPorPeriodoResponseDom.setPossuiCurso(false);
    }


    void faseParaCursoPorPeriodoFaseResponseDom(
            Fase fase,
            @MappingTarget CursoPorPeriodoFaseResponseDom fasePorPerido
    );

    @AfterMapping
    default void afterFaseParaCursoPorPeriodoFaseResponseDom(
            @MappingTarget CursoPorPeriodoFaseResponseDom fasePorPerido
    ){
        fasePorPerido.setPossuiFase(false);
    }
}
