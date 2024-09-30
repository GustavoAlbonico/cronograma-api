package com.cronograma.api.useCases.usuario;

import com.cronograma.api.entitys.NivelAcesso;
import com.cronograma.api.entitys.Usuario;
import com.cronograma.api.entitys.enums.StatusEnum;
import com.cronograma.api.exceptions.AuthorizationException;
import com.cronograma.api.exceptions.UsuarioException;
import com.cronograma.api.infra.security.TokenService;
import com.cronograma.api.useCases.usuario.domains.*;
import com.cronograma.api.useCases.usuario.implement.mappers.UsuarioMapper;
import com.cronograma.api.useCases.usuario.implement.repositorys.UsuarioNivelAcessoRepository;
import com.cronograma.api.useCases.usuario.implement.repositorys.UsuarioRepository;
import com.cronograma.api.utils.RegexUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;
    private final UsuarioNivelAcessoRepository usuarioNivelAcessoRepository;
    private final UsuarioMapper usuarioMapper;

    public UsuarioResponseDom login(UsuarioLoginRequestDom usuarioLoginRequestDom) {

        Usuario usuario = this.usuarioRepository.findByCpfAndStatusEnum(usuarioLoginRequestDom.cpf(), StatusEnum.ATIVO)
                .orElseThrow(() -> new AuthorizationException("Cpf não encontrado"));

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

    public UsuarioResponseDom cadastro(UsuarioCadastroRequestDom usuarioCadastroRequestDom) {
        usuarioCadastroRequestDom.setCpf(RegexUtil.retornarNumeros(usuarioCadastroRequestDom.getCpf()));
        List<String> errorMessages =  validarCamposCriar(usuarioCadastroRequestDom);

        if (errorMessages.isEmpty()){

            Set<NivelAcesso> niveisAcesso = new HashSet<>(usuarioNivelAcessoRepository.findAllById(usuarioCadastroRequestDom.getNiveisAcessoId()));
            Usuario usuario = usuarioMapper.usuarioRequestDomParaUsuario(usuarioCadastroRequestDom,niveisAcesso,passwordEncoder);
            usuarioRepository.save(usuario);

            List<UsuarioNivelAcessoResponseDom> niveisAcessoResponse = usuario.getNiveisAcesso().stream()
                    .map(nivelAcesso ->
                            new UsuarioNivelAcessoResponseDom(nivelAcesso.getNome(), nivelAcesso.getRankingAcesso()))
                    .toList();

            String token = this.tokenService.gerarToken(usuario);
            return new UsuarioResponseDom(usuario.getNome(), token,niveisAcessoResponse);
        }
        throw new UsuarioException(errorMessages);
    }

    public Usuario criarUsuario(UsuarioRequestDom usuarioRequestDom, String nomeNivelAcesso){
       Set<NivelAcesso> niveisAcesso = usuarioNivelAcessoRepository.findByNome(nomeNivelAcesso)
                .orElseThrow(() -> new UsuarioException("Nenhum nivel de acesso encontrado!"));

       Usuario usuario = usuarioMapper.usuarioRequestDomParaUsuario(usuarioRequestDom,niveisAcesso,passwordEncoder);
       return usuarioRepository.save(usuario);
    }

    public void editarUsuario(Long id,UsuarioRequestDom usuarioRequestDom){
        Usuario usuarioEncontrado = usuarioRepository.findById(id)
                .orElseThrow(() -> new UsuarioException("Nenhum usuario encontrado!"));

        usuarioMapper.usuarioRequestDomParaUsuarioEncontrado(usuarioRequestDom,usuarioEncontrado);
        usuarioRepository.save(usuarioEncontrado);
    }


    public void excluirUsuario(Long id){
        usuarioRepository.deleteById(id);
    }

    public Usuario buscarUsuarioAutenticado(){
        return (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    private List<String> validarCamposCriar(UsuarioRequestDom usuario){

        List<String> errorMessages =  new ArrayList<>();

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


        if(usuario.getSenha() == null || usuario.getSenha().isBlank()){
            errorMessages.add("Senha é um campo obrigatório!");
        } else {

           if(usuario.getSenha().length() < 8){
               errorMessages.add("Senha precisa conter no minimo 8 caracteres!");
           }
           if(!RegexUtil.existeCaracterEspecial(usuario.getSenha())){
               errorMessages.add("Senha precisa conter no minimo 1 caracter especial!");
           }
           if(!RegexUtil.existeLetraMaiuscula(usuario.getSenha())){
               errorMessages.add("Senha precisa conter no minimo 1 letra maiuscula!");
           }
           if(!RegexUtil.existeNumero(usuario.getSenha())){
               errorMessages.add("Senha precisa conter no minimo minimo 1 número!");
           }

        }
        return errorMessages;
    }
}
