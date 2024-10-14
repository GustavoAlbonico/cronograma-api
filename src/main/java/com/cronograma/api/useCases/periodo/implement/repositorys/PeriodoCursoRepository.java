package com.cronograma.api.useCases.periodo.implement.repositorys;

import com.cronograma.api.useCases.curso.implement.repositorys.CursoRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface PeriodoCursoRepository extends CursoRepository {

    @Query(value =
            "SELECT DISTINCT cronograma.curso_id " +
            "FROM dia_cronograma " +
            "JOIN cronograma ON dia_cronograma.cronograma_id = cronograma.id",
            nativeQuery = true)
    Set<Long> buscarTodosCursoIds();
}
