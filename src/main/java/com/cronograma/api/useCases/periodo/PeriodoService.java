package com.cronograma.api.useCases.periodo;

import com.cronograma.api.entitys.Periodo;
import com.cronograma.api.entitys.enums.StatusEnum;
import com.cronograma.api.exceptions.CronogramaException;
import com.cronograma.api.useCases.periodo.implement.repositorys.PeriodoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class PeriodoService {

    @Autowired
    private PeriodoRepository periodoRepository;
    public Periodo buscarPeriodoAtivoAtual() {
        Set<Periodo> periodoEncontrado =
                periodoRepository.findByStatusEnum(StatusEnum.ATIVO).orElseThrow(() -> new CronogramaException("Nenhum periodo encontrado!"));

        if(periodoEncontrado.size() > 1){
            throw new CronogramaException("Apenas um periodo pode estar ativo!");
        } else if (periodoEncontrado.isEmpty()) {
            throw new CronogramaException("É necessário ter um periodo ativo!");
        } else {
            return periodoEncontrado.stream().findFirst().orElseThrow(() -> new CronogramaException("Nenhum periodo encontrado!"));
        }
    }
}
