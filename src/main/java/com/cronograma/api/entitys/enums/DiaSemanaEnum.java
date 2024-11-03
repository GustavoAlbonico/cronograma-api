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

    public static String getDiaSemanaEnumLabel(DiaSemanaEnum diaSemanaEnum) {
        return switch (diaSemanaEnum) {
            case SEGUNDA_FEIRA -> "Segunda-Feira";
            case TERCA_FEIRA-> "Terça-Feira";
            case QUARTA_FEIRA-> "Quarta-Feira";
            case QUINTA_FEIRA-> "Quinta-Feira";
            case SEXTA_FEIRA-> "Sexta-Feira";
            case SABADO-> "Sábado";
            default -> throw new IllegalArgumentException("Dia da semana inválido: " + diaSemanaEnum);
        };
    }
}
