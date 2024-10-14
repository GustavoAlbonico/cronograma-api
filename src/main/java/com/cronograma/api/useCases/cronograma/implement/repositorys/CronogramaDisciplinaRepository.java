package com.cronograma.api.useCases.cronograma.implement.repositorys;

import com.cronograma.api.entitys.Disciplina;
import com.cronograma.api.entitys.enums.StatusEnum;
import com.cronograma.api.useCases.disciplina.implement.repositorys.DisciplinaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;
@Repository
public interface CronogramaDisciplinaRepository extends DisciplinaRepository {

    @Query(value =
            "SELECT DISTINCT disciplina.* " +
            "FROM dia_cronograma " +
            "JOIN disciplina ON dia_cronograma.disciplina_id = disciplina.id " +
            "JOIN cronograma ON dia_cronograma.cronograma_id = cronograma.id " +
            "WHERE cronograma.curso_id = :cursoId " +
            "AND dia_cronograma.fase_id = :faseId " +
            "AND cronograma.periodo_id = :periodoId",
            nativeQuery = true)
    List<Disciplina> buscarTodasDisciplinasPorPeriodoPorCursoIdPorFaseId(@Param("periodoId") Long periodoId, @Param("cursoId") Long cursoId, @Param("faseId") Long faseId);


    Optional<Set<Disciplina>> findAllByStatusEnumAndCursoId(StatusEnum statusEnum, Long id);
}
