package com.cronograma.api.useCases.periodo.implement.repositorys;

import com.cronograma.api.entitys.Periodo;
import com.cronograma.api.entitys.enums.StatusEnum;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.Set;
import org.springframework.stereotype.Repository;

@Repository
public interface PeriodoRepository extends JpaRepository<Periodo, Long> {

    Optional<Set<Periodo>> findByStatusEnum (StatusEnum statusEnum);
}
