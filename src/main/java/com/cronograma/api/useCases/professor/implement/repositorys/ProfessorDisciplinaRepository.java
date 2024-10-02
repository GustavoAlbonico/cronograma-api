package com.cronograma.api.useCases.professor.implement.repositorys;

import com.cronograma.api.useCases.disciplina.implement.repositorys.DisciplinaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProfessorDisciplinaRepository extends DisciplinaRepository {

    boolean existsByProfessorId(Long professorId);
}
