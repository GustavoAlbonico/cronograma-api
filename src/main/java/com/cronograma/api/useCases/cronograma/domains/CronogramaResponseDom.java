package com.cronograma.api.useCases.cronograma.domains;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CronogramaResponseDom {

    private Long id;
    private Long cursoId;
    private String cursoNome;
    private Integer faseNumero;
    private Integer ano;
    private List<CronogramaDisciplinaResponseDom> disciplinas = new ArrayList<>();
    private List<CronogramaMesResponseDom> meses =  new ArrayList<>();
}
