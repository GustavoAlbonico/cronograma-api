package com.cronograma.api.useCases.usuario.domains;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UsuarioRedefinirSenhaRequestDom {
    private String senhaAtual;
    private String senha;
    private String confirmarSenha;
}
