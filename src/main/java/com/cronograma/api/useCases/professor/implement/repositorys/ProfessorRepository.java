package com.cronograma.api.useCases.professor.implement.repositorys;

import com.cronograma.api.entitys.Professor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfessorRepository extends JpaRepository<Professor, Long> {
}
