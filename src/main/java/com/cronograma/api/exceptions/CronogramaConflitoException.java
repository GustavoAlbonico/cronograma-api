package com.cronograma.api.exceptions;

public class CronogramaConflitoException extends RuntimeException{

    public CronogramaConflitoException() {
        super("Conflito encontrado!");
    }

    public CronogramaConflitoException(String message) {
        super(message);
    }
}
