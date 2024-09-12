package com.cronograma.api.useCases.cronograma.implement.repositorys;

import com.cronograma.api.entitys.Cronograma;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CronogramaRepository extends JpaRepository<Cronograma, Long> {
}
