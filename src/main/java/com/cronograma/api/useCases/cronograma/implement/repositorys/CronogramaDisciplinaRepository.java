package com.cronograma.api.useCases.cronograma.implement.repositorys;

import com.cronograma.api.entitys.Disciplina;
import com.cronograma.api.useCases.disciplina.implement.repositorys.DisciplinaRepository;

import java.util.Optional;
import java.util.Set;

public interface CronogramaDisciplinaRepository extends DisciplinaRepository {

    Optional<Set<Disciplina>> findByCursoId(Long id);
}
