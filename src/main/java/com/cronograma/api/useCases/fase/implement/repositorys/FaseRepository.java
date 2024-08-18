package com.cronograma.api.useCases.fase.implement.repositorys;

import com.cronograma.api.entitys.Fase;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FaseRepository extends JpaRepository<Fase, Long> {
}
