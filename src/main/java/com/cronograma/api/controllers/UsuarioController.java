package com.cronograma.api.controllers;

import com.cronograma.api.useCases.usuario.UsuarioService;
import com.cronograma.api.useCases.usuario.domains.UsuarioCadastroRequestDom;
import com.cronograma.api.useCases.usuario.domains.UsuarioLoginRequestDom;
import com.cronograma.api.useCases.usuario.domains.UsuarioRedefinirSenhaRequestDom;
import com.cronograma.api.useCases.usuario.domains.UsuarioResponseDom;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/usuario")
public class UsuarioController {
    @Autowired
    UsuarioService usuarioService;


    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UsuarioLoginRequestDom usuarioLoginRequestDom){
        UsuarioResponseDom response = usuarioService.login(usuarioLoginRequestDom);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/cadastro")
    @PreAuthorize("@nivelAcessoService.validarNivelAcesso('USUARIO_CONTROLLER','CRIAR')")
    public ResponseEntity<?> cadastro(@RequestBody UsuarioCadastroRequestDom usuarioRequestDom){
        UsuarioResponseDom response =  usuarioService.cadastro(usuarioRequestDom);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/esqueciminhasenha/{cpf}")
    public ResponseEntity<?> esqueciMinhaSenha(@PathVariable String cpf) throws MessagingException {
        usuarioService.esqueciMinhaSenha(cpf);
        return ResponseEntity.ok(null);
    }

    @PostMapping("/redefinirsenha/validartoken")
    public ResponseEntity<?> redefinirSenhaValidarToken() {
        return ResponseEntity.ok(null);
    }

    @PutMapping("/redefinirsenha")
    @PreAuthorize("@nivelAcessoService.validarNivelAcesso('USUARIO_CONTROLLER','REDEFINIR_SENHA')")
    public ResponseEntity<?> redefinirsenha(@RequestBody UsuarioRedefinirSenhaRequestDom UsuarioRedefinirSenhaRequestDom) {
        usuarioService.redefinirsenha(UsuarioRedefinirSenhaRequestDom);
        return ResponseEntity.ok(null);
    }

}
