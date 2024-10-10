package com.cronograma.api.useCases.nivelAcesso;

import com.cronograma.api.entitys.Controller;
import com.cronograma.api.entitys.Funcionalidade;
import com.cronograma.api.entitys.NivelAcesso;
import com.cronograma.api.entitys.Usuario;
import com.cronograma.api.exceptions.AuthorizationException;
import com.cronograma.api.infra.listener.AuditListener;
import com.cronograma.api.useCases.usuario.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NivelAcessoService {

    private final UsuarioService usuarioService;

    private final AuditListener auditListener;

    public boolean validarNivelAcesso(String controller, String funcionalidade){

        Usuario usuario = usuarioService.buscarUsuarioAutenticado();

        for (NivelAcesso nivelAcesso: usuario.getNiveisAcesso()){
            for (Controller controllerNivelAcesso: nivelAcesso.getControllers()) {
                if (controllerNivelAcesso.getNome().equals(controller)){
                    for (Funcionalidade funcionalidadeNivelAcesso : controllerNivelAcesso.getFuncionalidades()){
                        if (funcionalidadeNivelAcesso.getNome().equals(funcionalidade)){
                            auditListener.setAcao(funcionalidade);
                            System.out.println(funcionalidade);
                            return true;
                        }
                    }
                }
            }
        }

        throw new AuthorizationException("Não possui nivel de acesso necessário!");
    }

}
