package com.cronograma.api.useCases.curso.implement.repositorys;

import com.cronograma.api.entitys.Curso;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CursoRepository extends JpaRepository<Curso, Long> {
}
