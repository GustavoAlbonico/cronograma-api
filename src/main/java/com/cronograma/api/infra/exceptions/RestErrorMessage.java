package com.cronograma.api.infra.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class RestErrorMessage {

    private HttpStatus status;
    private List<String> messages =  new ArrayList<>();

    public RestErrorMessage(HttpStatus status, List<String> messages) {
        this.status = status;
        this.messages = messages;
    }
    public RestErrorMessage(HttpStatus status, String message) {
        this.status = status;
        this.messages.add(message);
    }
}
