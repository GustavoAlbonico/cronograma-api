package com.cronograma.api.useCases.usuario;

import com.cronograma.api.entitys.NivelAcesso;
import com.cronograma.api.entitys.Usuario;
import com.cronograma.api.exceptions.AuthenticationException;
import com.cronograma.api.infra.security.TokenService;
import com.cronograma.api.useCases.usuario.domains.UsuarioRequestDom;
import com.cronograma.api.useCases.usuario.domains.UsuarioResponseDom;
import com.cronograma.api.useCases.usuario.implement.repositorys.UsuarioNivelAcessoRepository;
import com.cronograma.api.useCases.usuario.implement.repositorys.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;
    private final UsuarioNivelAcessoRepository usuarioNivelAcessoRepository;

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

            //criar validacao
            Usuario usuario = new Usuario();
            usuario.setSenha(passwordEncoder.encode(usuarioRequestDom.senha()));
            usuario.setCpf(usuarioRequestDom.cpf());
            usuario.setNome(usuarioRequestDom.nome());
            List<NivelAcesso> niveisAcesso = usuarioNivelAcessoRepository.findAllById(usuarioRequestDom.niveisAcessoId());
            usuario.setNiveisAcesso(new HashSet<>(niveisAcesso));

            usuarioRepository.save(usuario);

            String token = this.tokenService.gerarToken(usuario);
            return new UsuarioResponseDom(usuario.getNome(), token);
        }
        throw new AuthenticationException("Cpf já está sendo utilizado!");
    }
}
