package com.cronograma.api.useCases.usuario.domains;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UsuarioCadastroRequestDom extends UsuarioRequestDom{
    private List<Long> niveisAcessoId;
}
