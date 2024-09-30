package com.cronograma.api.useCases.usuario.domains;

import java.util.List;

public record UsuarioResponseDom (String nome, String token, List<UsuarioNivelAcessoResponseDom> niveisAcesso){}
