package com.cronograma.api.exceptions;

import java.util.ArrayList;
import java.util.List;

public class FaseException extends RuntimeException {
    private List<String> messageList =  new ArrayList<>();
    public FaseException(String mensagem){
        super(mensagem);
    }

    public FaseException(List<String> mensagens){
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
