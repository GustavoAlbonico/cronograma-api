package com.cronograma.api.entitys.enums;

import java.time.Month;

public enum MesEnum {
    JANEIRO,
    FEVEREIRO,
    MARCO,
    ABRIL,
    MAIO,
    JUNHO,
    JULHO,
    AGOSTO,
    SETEMBRO,
    OUTUBRO,
    NOVEMBRO,
    DEZEMBRO;

    public static MesEnum monthParaMesEnum(Month month) {
        return switch (month) {
            case JANUARY -> JANEIRO;
            case FEBRUARY -> FEVEREIRO;
            case MARCH -> MARCO;
            case APRIL -> ABRIL;
            case MAY -> MAIO;
            case JUNE -> JUNHO;
            case JULY -> JULHO;
            case AUGUST -> AGOSTO;
            case SEPTEMBER -> SETEMBRO;
            case OCTOBER -> OUTUBRO;
            case NOVEMBER -> NOVEMBRO;
            case DECEMBER -> DEZEMBRO;
        };
    }
}
