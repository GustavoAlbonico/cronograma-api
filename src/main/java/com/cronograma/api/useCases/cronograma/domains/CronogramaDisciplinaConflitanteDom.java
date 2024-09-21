package com.cronograma.api.useCases.cronograma.domains;

import com.cronograma.api.entitys.DiaSemanaDisponivel;
import com.cronograma.api.entitys.Disciplina;

public record CronogramaDisciplinaConflitanteDom (Disciplina disciplina, Disciplina disciplinaConflitante, DiaSemanaDisponivel diaSemanaDisponivelConflitante, int nivelConflito) {
}
