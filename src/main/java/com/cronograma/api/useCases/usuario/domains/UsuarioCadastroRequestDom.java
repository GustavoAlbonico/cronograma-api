package com.cronograma.api.useCases.usuario.domains;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UsuarioCadastroRequestDom{
    private List<Long> niveisAcessoId;
    private String cpf;
    private String senha;
    private String nome;
}
