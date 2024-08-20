package com.cronograma.api.useCases.periodo.implement.repositorys;

import com.cronograma.api.entitys.Periodo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PeriodoRepository extends JpaRepository<Periodo, Long> {
}
