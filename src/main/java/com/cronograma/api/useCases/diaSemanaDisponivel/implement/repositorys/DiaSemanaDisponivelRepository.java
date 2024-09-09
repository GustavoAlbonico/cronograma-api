package com.cronograma.api.useCases.diaSemanaDisponivel.implement.repositorys;

import com.cronograma.api.entitys.DiaSemanaDisponivel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DiaSemanaDisponivelRepository extends JpaRepository<DiaSemanaDisponivel, Long> {
}
