package com.cronograma.api.useCases.diaSemanaDisponivel.implement.repositorys;

import com.cronograma.api.entitys.DiaSemanaDisponivel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DiaSemanaDIsponivelRepository extends JpaRepository<DiaSemanaDisponivel, Long> {
}
