package com.cronograma.api.exceptions;

import java.util.ArrayList;
import java.util.List;

public class CoordenadorException extends RuntimeException{

    private List<String> messageList =  new ArrayList<>();
    public CoordenadorException(String mensagem){
        super(mensagem);
    }

    public CoordenadorException(List<String> mensagens){
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
