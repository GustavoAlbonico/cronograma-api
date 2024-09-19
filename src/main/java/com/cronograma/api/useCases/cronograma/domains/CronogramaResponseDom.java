package com.cronograma.api.useCases.cronograma.domains;

import com.cronograma.api.entitys.enums.DiaSemanaEnum;
import com.cronograma.api.entitys.enums.MesEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CronogramaResponseDom {

    private String cursoNome;
    private Integer faseNumero;
    private Integer ano;
    private List<CronogramaDisciplinaResponseDom> disciplinas = new ArrayList<>();
    private Map<MesEnum, Map<DiaSemanaEnum,List<CronogramaDiaCronogramaResponseDom>>> diasCronograma = new LinkedHashMap<>();
}
