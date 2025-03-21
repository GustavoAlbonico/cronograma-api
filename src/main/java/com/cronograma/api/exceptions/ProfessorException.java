package com.cronograma.api.exceptions;

import java.util.ArrayList;
import java.util.List;

public class ProfessorException extends RuntimeException{

    private List<String> messageList =  new ArrayList<>();
    public ProfessorException(String mensagem){
        super(mensagem);
    }

    public ProfessorException(List<String> mensagens){
        this.messageList = mensagens;
    }
    public List<String> getMessages(){
        if (messageList.isEmpty()){
            messageList.add(super.getMessage());
        }
        return messageList;
    }

    public String getMessage(){
        if(messageList.isEmpty()){
            return super.getMessage();
        }
        return messageList.toString();
    }
}
