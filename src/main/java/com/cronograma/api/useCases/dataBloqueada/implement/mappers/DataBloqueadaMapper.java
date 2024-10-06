package com.cronograma.api.useCases.dataBloqueada.implement.mappers;

import com.cronograma.api.entitys.DataBloqueada;
import com.cronograma.api.useCases.dataBloqueada.domains.DataBloqueadaRequestDom;
import com.cronograma.api.useCases.dataBloqueada.domains.DataBloqueadaResponseDom;
import org.mapstruct.*;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface DataBloqueadaMapper {

    DataBloqueada dataBloqueadaResquestDomParaDataBloqueada(DataBloqueadaRequestDom dataBloqueadaRequestDom);

    @Mapping(target = "id", ignore = true)
    void dataBloqueadaRequestDomParaDataBloqueadaEncontrada(
            DataBloqueadaRequestDom dataBloqueadaRequestDom,
            @MappingTarget DataBloqueada dataBloqueadaEncontrada
    );

    DataBloqueadaResponseDom dataBloqueadaParaDataBloqueadaResponseDom(DataBloqueada dataBloqueada);

}
