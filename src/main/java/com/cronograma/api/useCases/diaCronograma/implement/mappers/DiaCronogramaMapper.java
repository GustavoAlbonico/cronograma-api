package com.cronograma.api.useCases.diaCronograma.implement.mappers;

import com.cronograma.api.entitys.DiaCronograma;
import org.mapstruct.*;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface DiaCronogramaMapper {
    @Mapping(target = "dataStatusEnum", ignore = true)
    @Mapping(target = "disciplina", ignore = true)
    void diaCronogramaParaDiaCronogramaEditado(DiaCronograma diaCronograma, @MappingTarget DiaCronograma diaCronogramaNovaInstancia);
}
