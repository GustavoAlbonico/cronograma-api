package com.cronograma.api.useCases.usuario;

import com.cronograma.api.entitys.Usuario;
import com.cronograma.api.exceptions.AuthenticationException;
import com.cronograma.api.infra.security.TokenService;
import com.cronograma.api.useCases.usuario.domains.UsuarioRequestDom;
import com.cronograma.api.useCases.usuario.domains.UsuarioResponseDom;
import com.cronograma.api.useCases.usuario.implement.repositorys.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    public UsuarioResponseDom login(UsuarioRequestDom usuarioRequestDom) {
        Usuario usuario = this.usuarioRepository.findByCpf(usuarioRequestDom.cpf())
                .orElseThrow(() -> new AuthenticationException("Cpf não encontrado"));

        if (this.passwordEncoder.matches(usuario.getSenha(), usuarioRequestDom.senha())) {
            String token = this.tokenService.gerarToken(usuario);
            return new UsuarioResponseDom(usuario.getNome(), token);
        }
        throw new AuthenticationException("Senha inválida!");
    }

    public UsuarioResponseDom cadastro(UsuarioRequestDom usuarioRequestDom) {
        Optional<Usuario> usuarioEncontrado = this.usuarioRepository.findByCpf(usuarioRequestDom.cpf());

        if (usuarioEncontrado.isEmpty()){

            Usuario usuario = new Usuario();
            usuario.setSenha(passwordEncoder.encode(usuarioRequestDom.senha()));
            usuario.setCpf(usuarioRequestDom.cpf());
            usuario.setNome(usuarioRequestDom.nome());
            usuarioRepository.save(usuario);


            String token = this.tokenService.gerarToken(usuario);
            return new UsuarioResponseDom(usuario.getNome(), token);
        }
        throw new AuthenticationException("Cpf já está sendo utilizado!");
    }
}
