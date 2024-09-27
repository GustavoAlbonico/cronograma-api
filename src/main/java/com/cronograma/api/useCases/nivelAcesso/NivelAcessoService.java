package com.cronograma.api.useCases.nivelAcesso;

import com.cronograma.api.entitys.Usuario;
import com.cronograma.api.exceptions.AuthenticationException;
import com.cronograma.api.exceptions.AuthorizationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class NivelAcessoService {

    public boolean validarNivelAcesso(String controller, String funcionalidade){

        Usuario usuario = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        boolean possuiPermissao = usuario.getNiveisAcesso().stream()
                .anyMatch(nivelAcesso ->
                        nivelAcesso.getControllers().stream()
                                .anyMatch(controllerNivel ->
                                            controllerNivel.getNome().equals(controller) &&
                                            controllerNivel.getFuncionalidades().stream().anyMatch(
                                                  funcionalidadeNivel -> funcionalidadeNivel.getNome().equals(funcionalidade)
                                            )
                                )
                );

        if(!possuiPermissao) {
            throw new AuthorizationException("Não possui nivel de acesso necessário!");
        }

        return true;
    }
}
