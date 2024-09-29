package com.cronograma.api.controllers;

import com.cronograma.api.useCases.usuario.UsuarioService;
import com.cronograma.api.useCases.usuario.domains.UsuarioCadastroRequestDom;
import com.cronograma.api.useCases.usuario.domains.UsuarioLoginRequestDom;
import com.cronograma.api.useCases.usuario.domains.UsuarioResponseDom;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/usuario")
public class UsuarioController {
    @Autowired
    UsuarioService usuarioService;


    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UsuarioLoginRequestDom usuarioLoginRequestDom){
        UsuarioResponseDom response =  usuarioService.login(usuarioLoginRequestDom);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/cadastro")
    @PreAuthorize("@nivelAcessoService.validarNivelAcesso('USUARIO_CONTROLLER','CRIAR')")
    public ResponseEntity<?> cadastro(@RequestBody UsuarioCadastroRequestDom usuarioRequestDom){
        UsuarioResponseDom response =  usuarioService.cadastro(usuarioRequestDom);
        return ResponseEntity.ok(response);
    }

}
