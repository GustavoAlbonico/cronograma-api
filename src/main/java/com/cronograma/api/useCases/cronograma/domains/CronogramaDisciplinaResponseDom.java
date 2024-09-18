package com.cronograma.api.useCases.cronograma.domains;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CronogramaDisciplinaResponseDom {
    private String nome;
    private String sigla;
    private String corHexadecimal;
    private Double cargaHoraria;
    private String professorNome;
}
