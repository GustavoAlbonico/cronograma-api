package com.cronograma.api.useCases.curso.domains;

import com.cronograma.api.useCases.fase.domains.FaseResponseDom;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CursoPorPeriodoFaseResponseDom extends FaseResponseDom {
    private boolean possuiFase;
}
