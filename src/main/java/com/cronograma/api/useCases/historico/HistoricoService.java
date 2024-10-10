package com.cronograma.api.useCases.historico;

import com.cronograma.api.useCases.historico.domains.HistoricoRequestDom;
import org.springframework.stereotype.Service;

@Service
public class HistoricoService {

    public void criarHistorico(HistoricoRequestDom historicoRequestDom){

        System.out.println(historicoRequestDom);

    }
}
