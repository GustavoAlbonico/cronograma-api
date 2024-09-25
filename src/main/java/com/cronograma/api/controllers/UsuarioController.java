package com.cronograma.api.controllers;

import com.cronograma.api.entitys.Usuario;
import com.cronograma.api.exceptions.AuthenticationException;
import com.cronograma.api.infra.security.TokenService;
import com.cronograma.api.useCases.usuario.UsuarioService;
import com.cronograma.api.useCases.usuario.domains.UsuarioRequestDom;
import com.cronograma.api.useCases.usuario.domains.UsuarioResponseDom;
import com.cronograma.api.useCases.usuario.implement.repositorys.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.antlr.v4.runtime.Token;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
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
    public ResponseEntity<?> login(@RequestBody UsuarioRequestDom usuarioRequestDom){
        UsuarioResponseDom response =  usuarioService.login(usuarioRequestDom);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/cadastro")
    public ResponseEntity<?> cadastro(@RequestBody UsuarioRequestDom usuarioRequestDom){
        UsuarioResponseDom response =  usuarioService.cadastro(usuarioRequestDom);
        return ResponseEntity.ok(response);
    }

}
