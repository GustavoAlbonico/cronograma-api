package com.cronograma.api.controllers;

import com.cronograma.api.entitys.Usuario;
import com.cronograma.api.exceptions.AuthenticationException;
import com.cronograma.api.infra.security.TokenService;
import com.cronograma.api.useCases.usuario.domains.UsuarioRequestDom;
import com.cronograma.api.useCases.usuario.implement.repositorys.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.antlr.v4.runtime.Token;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/usuario")
@RequiredArgsConstructor
public class UsuarioController {
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    @PostMapping("/login")
    public ResponseEntity login(@RequestBody UsuarioRequestDom usuarioRequestDom){

        Usuario usuario = this.usuarioRepository.findByCpf(usuarioRequestDom.cpf())
                .orElseThrow( () -> new AuthenticationException("Usuario não encontrado"));

        if(passwordEncoder.matches(usuario.getSenha(),usuarioRequestDom.senha())){
            String token =  this.tokenService.gerarToken(usuario);
            return ResponseEntity.ok(null);
        }
    }
}
