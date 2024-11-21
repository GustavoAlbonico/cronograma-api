package com.cronograma.api.useCases.diaSemanaDisponivel.implement.repositorys;

import com.cronograma.api.entitys.DiaSemanaDisponivel;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;

@Repository
public interface DiaSemanaDisponivelRepository extends JpaRepository<DiaSemanaDisponivel, Long> {
}
