package com.cronograma.api.infra.security;

import com.cronograma.api.entitys.Usuario;
import com.cronograma.api.useCases.usuario.implement.repositorys.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UsuarioRepository usuarioRepository;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario usuario = this.usuarioRepository.findByCpf(username).orElseThrow(() -> new UsernameNotFoundException("Cpf não encontrado"));
        return new org.springframework.security.core.userdetails.User(usuario.getCpf(), usuario.getSenha(),new ArrayList<>());

    }
}
