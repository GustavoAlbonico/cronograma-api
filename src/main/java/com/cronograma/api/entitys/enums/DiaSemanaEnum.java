package com.cronograma.api.entitys.enums;

import java.time.DayOfWeek;

public enum DiaSemanaEnum {
    SEGUNDA_FEIRA,
    TERCA_FEIRA,
    QUARTA_FEIRA,
    QUINTA_FEIRA,
    SEXTA_FEIRA,
    SABADO,
    DOMINGO;

    public static DiaSemanaEnum dayOfWeekParaDiaSemanaEnum(DayOfWeek dayOfWeek) {
        return switch (dayOfWeek) {
            case MONDAY -> SEGUNDA_FEIRA;
            case TUESDAY -> TERCA_FEIRA;
            case WEDNESDAY -> QUARTA_FEIRA;
            case THURSDAY -> QUINTA_FEIRA;
            case FRIDAY -> SEXTA_FEIRA;
            case SATURDAY -> SABADO;
            default -> throw new IllegalArgumentException("Dia da semana inválido: " + dayOfWeek);
        };
    }
}
