package com.cronograma.api.utils;

public class RegexUtil {

    public static String retornarNumeros(String string){
        return string.replaceAll("\\D", "");
    }

    public static boolean existeCaracterEspecial(String string){
       return string.matches(".*[^a-zA-Z0-9].*");
    }

    public static boolean existeLetraMaiuscula(String string){
        return string.matches(".*[A-Z].*");
    }

    public static boolean existeNumero(String string){
        return string.matches(".*[0-9].*");
    }

    public static boolean validarEmail(String string){
        return string.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");
    }
}
