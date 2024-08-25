package com.cronograma.api.useCases.cronograma.domains;

import com.cronograma.api.entitys.Disciplina;
import com.cronograma.api.entitys.enums.DiaSemanaEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CronogramaDisciplinaDetalhadoDom{

    private Disciplina disciplina;
    private DiaSemanaEnum diaSemanaEnum;
    private double quantidadeDiasAulaRestantesPorDiaSemana;
    private double quantidadeDiasAulaRestantesNecessariosPorDisciplina;
    private double disciplinaPorcentagemOcupacaoDiasAulaPorDiaSemana;
}
