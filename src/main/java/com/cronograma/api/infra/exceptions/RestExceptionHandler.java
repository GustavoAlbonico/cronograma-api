package com.cronograma.api.infra.exceptions;

import com.cronograma.api.exceptions.*;
import jakarta.mail.AuthenticationFailedException;
import jakarta.mail.MessagingException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.io.IOException;
import java.net.SocketException;

@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(AuthenticationException.class)
    private ResponseEntity<RestErrorMessage> authenticationException(AuthenticationException exception){
        RestErrorMessage errorResponse = new RestErrorMessage(HttpStatus.FORBIDDEN,exception.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
    }
    @ExceptionHandler(AuthorizationException.class)
    private ResponseEntity<RestErrorMessage> authorizationException(AuthorizationException exception){
        RestErrorMessage errorResponse = new RestErrorMessage(HttpStatus.UNAUTHORIZED,exception.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
    }

    @ExceptionHandler(PaginacaoException.class)
    private ResponseEntity<RestErrorMessage> paginacaoException(PaginacaoException exception){
        RestErrorMessage errorResponse = new RestErrorMessage(HttpStatus.BAD_REQUEST,exception.getMessages());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

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

    @ExceptionHandler(FaseException.class)
    private ResponseEntity<RestErrorMessage> faseException(FaseException exception){
        RestErrorMessage errorResponse = new RestErrorMessage(HttpStatus.BAD_REQUEST,exception.getMessages());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(CoordenadorException.class)
    private ResponseEntity<RestErrorMessage> coordenadorException(CoordenadorException exception){
        RestErrorMessage errorResponse = new RestErrorMessage(HttpStatus.BAD_REQUEST,exception.getMessages());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(DataBloqueadaException.class)
    private ResponseEntity<RestErrorMessage> dataBloqueadaException(DataBloqueadaException exception){
        RestErrorMessage errorResponse = new RestErrorMessage(HttpStatus.BAD_REQUEST,exception.getMessages());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(AlunoException.class)
    private ResponseEntity<RestErrorMessage> alunoException(AlunoException exception){
        RestErrorMessage errorResponse = new RestErrorMessage(HttpStatus.BAD_REQUEST,exception.getMessages());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(IOException.class)
    private ResponseEntity<RestErrorMessage> alunoImportarException(IOException exception){
        RestErrorMessage errorResponse = new RestErrorMessage(HttpStatus.BAD_REQUEST,"Erro inesperado ao ler arquivo CSV!");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(ProfessorException.class)
    private ResponseEntity<RestErrorMessage> professorException(ProfessorException exception){
        RestErrorMessage errorResponse = new RestErrorMessage(HttpStatus.BAD_REQUEST,exception.getMessages());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(DisciplinaException.class)
    private ResponseEntity<RestErrorMessage> disciplinaException(DisciplinaException exception){
        RestErrorMessage errorResponse = new RestErrorMessage(HttpStatus.BAD_REQUEST,exception.getMessages());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(CursoException.class)
    private ResponseEntity<RestErrorMessage> cursoException(CursoException exception){
        RestErrorMessage errorResponse = new RestErrorMessage(HttpStatus.BAD_REQUEST,exception.getMessages());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }


    @ExceptionHandler(UsuarioException.class)
    private ResponseEntity<RestErrorMessage> usuarioException(UsuarioException exception){
        RestErrorMessage errorResponse = new RestErrorMessage(HttpStatus.BAD_REQUEST,exception.getMessages());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler({MessagingException.class, SocketException.class, AuthenticationFailedException.class})
    private ResponseEntity<RestErrorMessage> messagingException(){
        RestErrorMessage errorResponse = new RestErrorMessage(HttpStatus.BAD_REQUEST,"Erro inesperado ao enviar e-mail, tente novamente em alguns minutos!");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(Exception.class)
    private ResponseEntity<RestErrorMessage> exception(){
        RestErrorMessage errorResponse = new RestErrorMessage(HttpStatus.INTERNAL_SERVER_ERROR,"Erro não mapeado!");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
}
