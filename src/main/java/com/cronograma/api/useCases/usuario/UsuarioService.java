package com.cronograma.api.useCases.usuario;

import com.cronograma.api.entitys.NivelAcesso;
import com.cronograma.api.entitys.Usuario;
import com.cronograma.api.entitys.enums.StatusEnum;
import com.cronograma.api.exceptions.AuthorizationException;
import com.cronograma.api.exceptions.UsuarioException;

import com.cronograma.api.infra.mensageria.EmailService;
import com.cronograma.api.infra.security.TokenService;
import com.cronograma.api.useCases.usuario.domains.*;
import com.cronograma.api.useCases.usuario.implement.mappers.UsuarioMapper;
import com.cronograma.api.useCases.usuario.implement.repositorys.UsuarioNivelAcessoRepository;
import com.cronograma.api.useCases.usuario.implement.repositorys.UsuarioRepository;
import com.cronograma.api.utils.SenhaPadraoUtil;
import com.cronograma.api.utils.regex.RegexUtil;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioNivelAcessoRepository usuarioNivelAcessoRepository;

    private final PasswordEncoder passwordEncoder;

    private final TokenService tokenService;

    private final EmailService emailService;
    private final UsuarioMapper usuarioMapper;

    public UsuarioResponseDom login(UsuarioLoginRequestDom usuarioLoginRequestDom) {

        Usuario usuario = this.usuarioRepository.findByCpfAndStatusEnum(usuarioLoginRequestDom.cpf(), StatusEnum.ATIVO)
                .orElseThrow(() -> new AuthorizationException("Cpf não encontrado"));

        if(verificarPrimeiroLogin(usuarioLoginRequestDom,usuario)){
            return null;
        }

        if (this.passwordEncoder.matches(usuarioLoginRequestDom.senha(), usuario.getSenha())) {
            String token = this.tokenService.gerarToken(usuario);

            List<UsuarioNivelAcessoResponseDom> niveisAcessoResponse = usuario.getNiveisAcesso().stream()
                    .map(nivelAcesso ->
                            new UsuarioNivelAcessoResponseDom(nivelAcesso.getNome(), nivelAcesso.getRankingAcesso()))
                    .toList();

            return new UsuarioResponseDom(usuario.getNome(), token, niveisAcessoResponse);
        }
        throw new AuthorizationException("Senha inválida!");
    }

    private boolean verificarPrimeiroLogin(UsuarioLoginRequestDom usuarioLoginRequestDom, Usuario usuario){
        return this.passwordEncoder.matches(SenhaPadraoUtil.getSenha(), usuario.getSenha()) &&
                usuarioLoginRequestDom.senha().equals(SenhaPadraoUtil.getSenha());
    }

    public UsuarioResponseDom cadastro(UsuarioCadastroRequestDom usuarioCadastroRequestDom) {
        usuarioCadastroRequestDom.setCpf(RegexUtil.retornarNumeros(usuarioCadastroRequestDom.getCpf()));
        validarCampos(usuarioCadastroRequestDom);

        Set<NivelAcesso> niveisAcesso = new HashSet<>(usuarioNivelAcessoRepository.findAllById(usuarioCadastroRequestDom.getNiveisAcessoId()));
        Usuario usuario = usuarioMapper.usuarioCadastroRequestDomParaUsuario(usuarioCadastroRequestDom,niveisAcesso,passwordEncoder);
        usuarioRepository.save(usuario);

        List<UsuarioNivelAcessoResponseDom> niveisAcessoResponse = usuario.getNiveisAcesso().stream()
                 .map(nivelAcesso ->
                         new UsuarioNivelAcessoResponseDom(nivelAcesso.getNome(), nivelAcesso.getRankingAcesso()))
                 .toList();

        String token = this.tokenService.gerarToken(usuario);
        return new UsuarioResponseDom(usuario.getNome(), token,niveisAcessoResponse);
    }

    public Usuario criarUsuario(UsuarioRequestDom usuarioRequestDom, String nomeNivelAcesso){
       Set<NivelAcesso> niveisAcesso = usuarioNivelAcessoRepository.findByNome(nomeNivelAcesso)
               .filter(set -> !set.isEmpty())
                .orElseThrow(() -> new UsuarioException("Nenhum nivel de acesso encontrado!"));

       Usuario usuario = usuarioMapper.usuarioRequestDomParaUsuario(usuarioRequestDom,niveisAcesso,passwordEncoder);
       return usuarioRepository.save(usuario);
    }

    public Usuario criarUsuarioImportar(UsuarioRequestDom usuarioRequestDom,  Set<NivelAcesso> niveisAcesso){
        Usuario usuario = usuarioMapper.usuarioRequestDomParaUsuario(usuarioRequestDom,niveisAcesso,passwordEncoder);
        return usuarioRepository.save(usuario);
    }

    public void editarUsuario(Long id,UsuarioRequestDom usuarioRequestDom){
        Usuario usuarioEncontrado = usuarioRepository.findById(id)
                .orElseThrow(() -> new UsuarioException("Nenhum usuario encontrado!"));

        usuarioMapper.usuarioRequestDomParaUsuarioEncontrado(usuarioRequestDom,usuarioEncontrado);
        usuarioRepository.save(usuarioEncontrado);
    }

    public void editarUsuarioNivelAcesso(Long id, Set<NivelAcesso> niveisAcesso){
        Usuario usuarioEncontrado = usuarioRepository.findById(id)
                .orElseThrow(() -> new UsuarioException("Nenhum usuario encontrado!"));

        usuarioEncontrado.setNiveisAcesso(niveisAcesso);
        usuarioRepository.save(usuarioEncontrado);
    }

    public void esqueciMinhaSenha(String cpf) throws MessagingException {
        Usuario usuario = this.usuarioRepository.findByCpfAndStatusEnum(RegexUtil.retornarNumeros(cpf), StatusEnum.ATIVO)
                .orElseThrow(() -> new AuthorizationException("Cpf não encontrado"));
        emailService.enviarEmailEsqueciMinhaSenha(usuario);
    }
    public void redefinirSenhaEmail(UsuarioRedefinirSenhaEmailRequestDom usuarioRedefinirSenhaEmailRequestDom){
        Usuario usuario = this.buscarUsuarioAutenticado();

        List<String> errorMessages = validarSenha(usuarioRedefinirSenhaEmailRequestDom.getSenha(),usuario.getSenha());

        if(!usuarioRedefinirSenhaEmailRequestDom.getSenha().equals(usuarioRedefinirSenhaEmailRequestDom.getConfirmarSenha())){
            errorMessages.add("As senhas precisam ser iguais");
        }

        if(!errorMessages.isEmpty()){
            throw new UsuarioException(errorMessages);
        }

        usuario.setSenha(passwordEncoder.encode(usuarioRedefinirSenhaEmailRequestDom.getSenha()));
        usuarioRepository.save(usuario);
    }

    public void redefinirSenha(UsuarioRedefinirSenhaRequestDom usuarioRedefinirSenhaRequestDom){
        Usuario usuario = this.buscarUsuarioAutenticado();

        List<String> errorMessages = validarSenha(usuarioRedefinirSenhaRequestDom.getSenha(),usuario.getSenha());

        if(!usuarioRedefinirSenhaRequestDom.getSenha().equals(usuarioRedefinirSenhaRequestDom.getConfirmarSenha())){
            errorMessages.add("As senhas precisam ser iguais");
        }

        if (!this.passwordEncoder.matches(usuarioRedefinirSenhaRequestDom.getSenhaAtual(), usuario.getSenha())){
            errorMessages.add("Senha atual inválida!");
        }

        if(!errorMessages.isEmpty()){
            throw new UsuarioException(errorMessages);
        }

        usuario.setSenha(passwordEncoder.encode(usuarioRedefinirSenhaRequestDom.getSenha()));
        usuarioRepository.save(usuario);
    }

    public void inativarUsuario(Long id){
        Usuario usuarioEncontrado = usuarioRepository.findById(id)
                .orElseThrow(() -> new UsuarioException("Nenhum usuario encontrado!"));
        usuarioEncontrado.setStatusEnum(StatusEnum.INATIVO);
        usuarioRepository.save(usuarioEncontrado);
    }

    public void ativarUsuario(Long id){
        Usuario usuarioEncontrado = usuarioRepository.findById(id)
                .orElseThrow(() -> new UsuarioException("Nenhum usuario encontrado!"));
        usuarioEncontrado.setStatusEnum(StatusEnum.ATIVO);
        usuarioRepository.save(usuarioEncontrado);
    }

    public void excluirUsuario(Long id){
        usuarioRepository.deleteById(id);
    }

    public Usuario buscarUsuarioAutenticado(){
        Long usuarioId = ((Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();
        return usuarioRepository.findById(usuarioId).get();
    }

    private void validarCampos(UsuarioCadastroRequestDom usuario){
        List<String> errorMessages =  new ArrayList<>(validarSenha(usuario.getSenha(),null));

        if(usuario.getNome() == null || usuario.getNome().isBlank()){
            errorMessages.add("Nome é um campo obrigatório!");
        }

        if(usuario.getCpf() == null || RegexUtil.retornarNumeros(usuario.getCpf()).isBlank()){
            errorMessages.add("Cpf é um campo obrigatório!");
        } else if(RegexUtil.retornarNumeros(usuario.getCpf()).length() != 11){
            errorMessages.add("Cpf inválido!");
        } else if (usuarioRepository.existsByCpf(RegexUtil.retornarNumeros(usuario.getCpf()))) {
            errorMessages.add("Cpf já está sendo utilizado!");
        }

        if(!errorMessages.isEmpty()){
            throw new UsuarioException(errorMessages);
        }
    }

    private List<String> validarSenha(String senha, String senhaAtual){
        List<String> errorMessages =  new ArrayList<>();

        if(senha == null || senha.isBlank()){
            errorMessages.add("Senha é um campo obrigatório!");
        } else {
            if(senha.length() < 8){
                errorMessages.add("Senha precisa conter no minimo 8 caracteres!");
            }
            if(!RegexUtil.existeCaracterEspecial(senha)){
                errorMessages.add("Senha precisa conter no minimo 1 caracter especial!");
            }
            if(!RegexUtil.existeLetraMaiuscula(senha)){
                errorMessages.add("Senha precisa conter no minimo 1 letra maiuscula!");
            }
            if(!RegexUtil.existeNumero(senha)){
                errorMessages.add("Senha precisa conter no minimo minimo 1 número!");
            }
            if (senhaAtual != null){
                if (this.passwordEncoder.matches(senha, senhaAtual)){
                    errorMessages.add("Nova Senha precisa ser diferente da antiga senha!");
                }
            }
        }
        return errorMessages;
    }
}
