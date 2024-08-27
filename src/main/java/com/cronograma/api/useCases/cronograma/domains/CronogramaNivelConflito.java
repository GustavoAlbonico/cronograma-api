package com.cronograma.api.useCases.cronograma.domains;

import com.cronograma.api.entitys.enums.DiaSemanaEnum;

public record CronogramaNivelConflito(int nivelConflitoPai, int nivelConflitoFilho, DiaSemanaEnum diaSemanaEnum) {
}
