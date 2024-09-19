package com.cronograma.api.useCases.cronograma.implement.repositorys;

import com.cronograma.api.entitys.Fase;
import com.cronograma.api.useCases.fase.implement.repositorys.FaseRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface CronogramaFaseRepository extends FaseRepository {
    @Query(value =
            "SELECT fase.* " +
            "FROM fase " +
            "JOIN disciplina ON fase.id = disciplina.fase_id " +
            "WHERE disciplina.curso_id = :cursoId " +
            "GROUP BY fase.id " +
            "ORDER BY fase.numero ASC;",
            nativeQuery = true)
    Optional<List<Fase>> buscarFasesPorCursoId(@Param("cursoId") Long cursoId);
}
