package com.cronograma.api.useCases.cronograma.implement.repositorys;

import com.cronograma.api.entitys.DiaCronograma;
import com.cronograma.api.useCases.diaCronograma.implement.repositorys.DiaCronogramaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CronogramaDiaCronogramaRepository extends DiaCronogramaRepository {
    @Query(value =
            "SELECT dia_cronograma.* " +
                    "FROM dia_cronograma " +
                    "JOIN cronograma ON dia_cronograma.cronograma_id = cronograma.id " +
                    "WHERE cronograma.curso_id = :cursoId " +
                    "AND dia_cronograma.fase_id = :faseId " +
                    "GROUP BY dia_cronograma.id;",
            nativeQuery = true)
    List<DiaCronograma> buscarTodosPorCursoIdFaseId(Long cursoId, Long faseId);
}
