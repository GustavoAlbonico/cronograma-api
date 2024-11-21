package com.cronograma.api.useCases.curso.implement.mappers;

import com.cronograma.api.entitys.Curso;
import com.cronograma.api.entitys.Fase;
import com.cronograma.api.entitys.enums.StatusEnum;
import com.cronograma.api.useCases.curso.domains.CursoPorUsuarioResponseDom;
import org.mapstruct.*;

import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.stream.Collectors;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface CursoPorUsuarioMapper {


    @BeforeMapping
    default void beforeCursoParaCursoPorUsuarioResponseDom(Curso curso) {
        if (curso != null){
            curso.setFases(
                    curso.getFases().stream()
                            .filter(fase -> fase.getStatusEnum().equals(StatusEnum.ATIVO))
                            .sorted(Comparator.comparing(Fase::getNumero))
                            .collect(Collectors.toCollection(LinkedHashSet::new))
            );
        }
    }

    CursoPorUsuarioResponseDom cursoParaCursoPorUsuarioResponseDom(Curso curso);
}
