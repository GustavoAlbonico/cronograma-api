package com.cronograma.api.useCases.usuario.domains;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UsuarioRedefinirSenhaEmailRequestDom {
    private String senha;
    private String confirmarSenha;
}
