package com.cronograma.api.useCases.fase.implement.repositorys;

import com.cronograma.api.entitys.Fase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.Set;

public interface FaseRepository extends JpaRepository<Fase, Long> {
    @Query(value =
            "SELECT fase.* " +
            "FROM fase " +
            "JOIN disciplina ON fase.id = disciplina.fase_id " +
            "WHERE disciplina.curso_id = :cursoId " +
            "AND NOT EXISTS (" +
                "SELECT 1 " +
                "FROM disciplina d " +
                "WHERE d.fase_id = disciplina.fase_id " +
                "AND d.professor_id IS NULL " +
            ") " +
            "GROUP BY fase.id;",
        nativeQuery = true)
    Optional<Set<Fase>> buscarFasesPorCursoOndeExisteProfessorId(@Param("cursoId") Long cursoId);


}
