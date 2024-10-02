package com.cronograma.api.utils;

import java.time.LocalDate;

public class SenhaPadraoUtil {

    private static final String SENHA_PADRAO = "SENACPLAN" + LocalDate.now().getYear();

    public static String getSenha(){
        return SENHA_PADRAO;
    }
}
