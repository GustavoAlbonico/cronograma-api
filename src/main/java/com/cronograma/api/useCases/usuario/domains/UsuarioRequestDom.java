package com.cronograma.api.useCases.usuario.domains;

import com.cronograma.api.entitys.enums.NivelAcessoEnum;

public record UsuarioRequestDom (String cpf, String senha, String nome, NivelAcessoEnum nivelAcessoEnum){
}
