package com.cronograma.api.infra.exceptions;

import com.cronograma.api.exceptions.CampoObrigatorioException;
import com.cronograma.api.exceptions.CronogramaException;
import com.cronograma.api.exceptions.DiaCronogramaException;
import com.cronograma.api.exceptions.EventoException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(CronogramaException.class)
    private ResponseEntity<RestErrorMessage> cronogramaException(CronogramaException exception){
        RestErrorMessage errorResponse = new RestErrorMessage(HttpStatus.BAD_REQUEST,exception.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(EventoException.class)
    private ResponseEntity<RestErrorMessage> eventoException(EventoException exception){
        RestErrorMessage errorResponse = new RestErrorMessage(HttpStatus.BAD_REQUEST,exception.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(DiaCronogramaException.class)
    private ResponseEntity<RestErrorMessage> diaCronogramaException(DiaCronogramaException exception){
        RestErrorMessage errorResponse = new RestErrorMessage(HttpStatus.BAD_REQUEST,exception.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(CampoObrigatorioException.class)
    private ResponseEntity<RestErrorMessage> campoObrigatorioException(CampoObrigatorioException exception){
        RestErrorMessage errorResponse = new RestErrorMessage(HttpStatus.BAD_REQUEST,exception.getMessages());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(Exception.class)
    private ResponseEntity<RestErrorMessage> exception(){
        RestErrorMessage errorResponse = new RestErrorMessage(HttpStatus.INTERNAL_SERVER_ERROR,"Erro não mapeado!");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
}
