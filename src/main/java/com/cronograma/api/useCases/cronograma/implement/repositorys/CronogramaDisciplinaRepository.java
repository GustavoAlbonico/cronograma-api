package com.cronograma.api.useCases.cronograma.implement.repositorys;

import com.cronograma.api.entitys.Disciplina;
import com.cronograma.api.useCases.disciplina.implement.repositorys.DisciplinaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;
@Repository
public interface CronogramaDisciplinaRepository extends DisciplinaRepository {

    Optional<Set<Disciplina>> findByCursoId(Long id);
}
