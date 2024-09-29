package com.cronograma.api.useCases.usuario;

import com.cronograma.api.entitys.NivelAcesso;
import com.cronograma.api.entitys.Usuario;
import com.cronograma.api.exceptions.AuthorizationException;
import com.cronograma.api.infra.security.TokenService;
import com.cronograma.api.useCases.usuario.domains.UsuarioLoginRequestDom;
import com.cronograma.api.useCases.usuario.domains.UsuarioCadastroRequestDom;
import com.cronograma.api.useCases.usuario.domains.UsuarioResponseDom;
import com.cronograma.api.useCases.usuario.implement.repositorys.UsuarioNivelAcessoRepository;
import com.cronograma.api.useCases.usuario.implement.repositorys.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;
    private final UsuarioNivelAcessoRepository usuarioNivelAcessoRepository;

    public UsuarioResponseDom login(UsuarioLoginRequestDom usuarioLoginRequestDom) {
        Usuario usuario = this.usuarioRepository.findByCpf(usuarioLoginRequestDom.cpf())
                .orElseThrow(() -> new AuthorizationException("Cpf não encontrado"));

        if (this.passwordEncoder.matches(usuarioLoginRequestDom.senha(), usuario.getSenha())) {
            String token = this.tokenService.gerarToken(usuario);
            return new UsuarioResponseDom(usuario.getNome(), token);
        }
        throw new AuthorizationException("Senha inválida!");
    }

    public UsuarioResponseDom cadastro(UsuarioCadastroRequestDom usuarioCadastroRequestDom) {
        Optional<Usuario> usuarioEncontrado = this.usuarioRepository.findByCpf(usuarioCadastroRequestDom.cpf());

        if (usuarioEncontrado.isEmpty()){

            Usuario usuario = new Usuario();
            usuario.setSenha(passwordEncoder.encode(usuarioCadastroRequestDom.senha()));
            usuario.setCpf(usuarioCadastroRequestDom.cpf());
            usuario.setNome(usuarioCadastroRequestDom.nome());
            List<NivelAcesso> niveisAcesso = usuarioNivelAcessoRepository.findAllById(usuarioCadastroRequestDom.niveisAcessoId());
            usuario.setNiveisAcesso(new HashSet<>(niveisAcesso));

            usuarioRepository.save(usuario);

            String token = this.tokenService.gerarToken(usuario);
            return new UsuarioResponseDom(usuario.getNome(), token);
        }
        throw new AuthorizationException("Cpf já está sendo utilizado!");
    }

    public Usuario buscarUsuarioAutenticado(){
        return (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
