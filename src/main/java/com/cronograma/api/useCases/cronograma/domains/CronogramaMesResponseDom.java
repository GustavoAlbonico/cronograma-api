package com.cronograma.api.useCases.cronograma.domains;

import com.cronograma.api.entitys.enums.DiaSemanaEnum;
import com.cronograma.api.entitys.enums.MesEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CronogramaMesResponseDom {
    private MesEnum mesEnum;
    private Map<DiaSemanaEnum,List<CronogramaDiaCronogramaResponseDom>> diasSemana =  new LinkedHashMap<>();
}
