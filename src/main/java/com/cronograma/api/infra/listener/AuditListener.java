package com.cronograma.api.infra.listener;

import com.cronograma.api.entitys.Disciplina;

import com.cronograma.api.useCases.historico.HistoricoService;
import com.cronograma.api.useCases.historico.domains.HistoricoRequestDom;
import com.cronograma.api.useCases.periodo.PeriodoService;
import com.cronograma.api.useCases.usuario.UsuarioService;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor(onConstructor_ = @__(@Lazy))
public class AuditListener {

    private final HistoricoService historicoService;
    private final UsuarioService usuarioService;
    private final PeriodoService periodoService;

    private static String acao;

    public void setAcao(String acao) {
        this.acao = acao;
    }

    public String getAcao() {
        return acao;
    }

    @PrePersist
    private void prePersist(Object entidade){


        switch (entidade){
            case Disciplina disciplina -> {
                historicoService.criarHistorico(
                        new HistoricoRequestDom(
                                usuarioService.buscarNomeUsuarioAutenticado(),
                                disciplina.getNome(),
                                this.getAcao(),
                                disciplina.getCurso().getId(),
                                periodoService.buscarPeriodoAtivoAtual().getId()
                        )
                );
            }
            default -> {
                System.out.println("ERRO!");
            }
        }
    }

    @PreUpdate
    private void preUpdate(Object entidade){


        switch (entidade){
            case Disciplina disciplina -> {
                historicoService.criarHistorico(
                        new HistoricoRequestDom(
                                usuarioService.buscarNomeUsuarioAutenticado(),
                                disciplina.getNome(),
                                this.getAcao(),
                                disciplina.getCurso().getId(),
                                periodoService.buscarPeriodoAtivoAtual().getId()
                        )
                );
            }
            default -> {
                System.out.println("ERRO!");
            }
        }
    }
}
