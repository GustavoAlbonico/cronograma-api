package com.cronograma.api.useCases.cronograma.domains;

import com.cronograma.api.entitys.Disciplina;
import com.cronograma.api.entitys.enums.DiaSemanaEnum;

import java.util.Map;

public record CronogramaDisciplinaDiasAulaDom(Disciplina disciplina, double quantidadeDiasAulaNecessariosPorDisciplina, Map<DiaSemanaEnum, Double> disciplinaQuantidadeDiasAulaPorSemana ) {
}
