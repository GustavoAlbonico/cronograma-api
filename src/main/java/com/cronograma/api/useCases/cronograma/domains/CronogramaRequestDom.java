package com.cronograma.api.useCases.cronograma.domains;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CronogramaRequestDom{
    private Long periodoId;
    private Long cursoId;
}