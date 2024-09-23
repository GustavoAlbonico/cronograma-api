package com.cronograma.api.useCases.fase.implement.mappers;

import com.cronograma.api.entitys.Fase;
import com.cronograma.api.useCases.fase.domains.FaseRequestDom;
import com.cronograma.api.useCases.fase.domains.FaseResponseDom;
import org.mapstruct.*;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface FaseMapper {


    Fase faseRequestDomParaFase(FaseRequestDom faseRequestDom);

    @Mapping(target = "id", ignore = true)
    void faseRequestDomParaFaseEncontrada(FaseRequestDom faseRequestDom,
                                           @MappingTarget Fase fase);

    FaseResponseDom faseParaFaseResponseDom(Fase fase);
}
