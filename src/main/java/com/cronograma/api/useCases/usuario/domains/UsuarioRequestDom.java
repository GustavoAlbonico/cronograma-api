package com.cronograma.api.useCases.usuario.domains;

import java.util.List;

public record UsuarioRequestDom (String cpf, String senha, String nome, List<Long> niveisAcessoId){
}
