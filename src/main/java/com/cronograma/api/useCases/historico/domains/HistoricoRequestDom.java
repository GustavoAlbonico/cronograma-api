package com.cronograma.api.useCases.historico.domains;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class HistoricoRequestDom {
    private String usuarioNome;
    private String objetoAcao;
    private String acaoEfetuada;
    private Long cursoId;
    private Long periodoId;

    @Override
    public String toString() {
        return "HistoricoRequestDom{" +
                "usuarioNome='" + usuarioNome + '\'' +
                ", objetoAcao='" + objetoAcao + '\'' +
                ", acaoEfetuada='" + acaoEfetuada + '\'' +
                ", cursoId=" + cursoId +
                ", periodoId=" + periodoId +
                '}';
    }
}
