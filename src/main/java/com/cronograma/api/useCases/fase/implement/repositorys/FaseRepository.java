package com.cronograma.api.useCases.fase.implement.repositorys;

import com.cronograma.api.entitys.Fase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface FaseRepository extends JpaRepository<Fase, Long> {
    @Query(value =
            "SELECT fase.* " +
            "FROM fase " +
            "JOIN disciplina ON fase.id = disciplina.fase_id " +
            "WHERE disciplina.curso_id = :cursoId " +
            "GROUP BY fase.id " +
            "ORDER BY fase.numero DESC;",
        nativeQuery = true)
    Optional<List<Fase>> buscarFasesPorCursoId(@Param("cursoId") Long cursoId);


}
