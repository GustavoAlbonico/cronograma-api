package com.cronograma.api.useCases.curso.implement.repositorys;
import com.cronograma.api.entitys.Cronograma;
import com.cronograma.api.useCases.cronograma.implement.repositorys.CronogramaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CursoCronogramaRepository extends CronogramaRepository {

    List<Cronograma> findAllByPeriodoId(Long periodoId);
}
