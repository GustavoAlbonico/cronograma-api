package com.cronograma.api.exceptions;

import java.util.ArrayList;
import java.util.List;

public class CampoObrigatorioException extends RuntimeException{

    private List<String> messageList =  new ArrayList<>();
    public CampoObrigatorioException(String mensagem){
        super(mensagem);
    }

    public CampoObrigatorioException(List<String> mensagens){
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
