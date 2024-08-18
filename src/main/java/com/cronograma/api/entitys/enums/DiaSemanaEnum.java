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
        switch (dayOfWeek) {
            case MONDAY:
                return SEGUNDA_FEIRA;
            case TUESDAY:
                return TERCA_FEIRA;
            case WEDNESDAY:
                return QUARTA_FEIRA;
            case THURSDAY:
                return QUINTA_FEIRA;
            case FRIDAY:
                return SEXTA_FEIRA;
            case SATURDAY:
                return SABADO;
            case SUNDAY:
                return DOMINGO;
            default:
                throw new IllegalArgumentException("Dia da semana inválido: " + dayOfWeek);
        }
    }
}
