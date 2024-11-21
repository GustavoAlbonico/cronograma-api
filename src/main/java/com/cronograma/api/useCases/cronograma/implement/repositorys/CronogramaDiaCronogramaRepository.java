package com.cronograma.api.useCases.cronograma.implement.repositorys;

import com.cronograma.api.entitys.DiaCronograma;
import com.cronograma.api.useCases.diaCronograma.implement.repositorys.DiaCronogramaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface CronogramaDiaCronogramaRepository extends DiaCronogramaRepository {
    @Query(value =
            "SELECT dia_cronograma.* " +
            "FROM dia_cronograma " +
            "JOIN cronograma ON dia_cronograma.cronograma_id = cronograma.id " +
            "WHERE cronograma.curso_id = :cursoId " +
            "AND dia_cronograma.fase_id = :faseId " +
            "AND cronograma.periodo_id = :periodoId " +
            "GROUP BY dia_cronograma.id",
            nativeQuery = true)
    List<DiaCronograma> buscarTodosPorPeriodoPorCursoIdPorFaseId(@Param("periodoId") Long periodoId,@Param("cursoId") Long cursoId,@Param("faseId") Long faseId);

    @Query(value =
            "SELECT dia_cronograma.* " +
            "FROM dia_cronograma " +
            "JOIN disciplina ON dia_cronograma.disciplina_id = disciplina.id " +
            "JOIN cronograma ON dia_cronograma.cronograma_id = cronograma.id " +
            "WHERE disciplina.professor_id IN :professoresId " +
            "AND cronograma.periodo_id = :periodoId " +
            "GROUP BY dia_cronograma.id",
    nativeQuery = true)
    List<DiaCronograma> buscarTodosPorPeriodoIdPorProfessoresId(@Param("periodoId") Long periodoId, @Param("professoresId") Set<Long> professoresId);

    void deleteAllByCronogramaId(Long cronogramaId);
}
