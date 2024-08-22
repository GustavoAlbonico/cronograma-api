package com.cronograma.api.useCases.curso.implement.repositorys;

import com.cronograma.api.entitys.Curso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface CursoRepository extends JpaRepository<Curso, Long> {
}
