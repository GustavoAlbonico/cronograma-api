package com.cronograma.api.useCases.nivelAcesso;

import com.cronograma.api.entitys.Usuario;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class NivelAcessoService {

    public boolean validarNivelAcesso(String controller, String funcionalidade){


        Usuario usuario = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        System.out.println(usuario.getNome());


        return false;
    }
}
