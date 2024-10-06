package com.cronograma.api.useCases.dataBloqueada.domains;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class DataBloqueadaRequestDom {
    private String motivo;
    private LocalDate data;
}
