package com.cronograma.api.useCases.disciplina.implement.repositorys;

import com.cronograma.api.entitys.Disciplina;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.Set;

public interface DisciplinaRepository extends JpaRepository<Disciplina, Long> {

    Optional<Set<Disciplina>> findByCursoId(Long id);
    Optional<Set<Disciplina>> findByFaseId(Long id);
}
