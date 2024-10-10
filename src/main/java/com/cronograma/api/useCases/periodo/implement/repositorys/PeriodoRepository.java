package com.cronograma.api.useCases.periodo.implement.repositorys;

import com.cronograma.api.entitys.Periodo;
import com.cronograma.api.entitys.enums.StatusEnum;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PeriodoRepository extends JpaRepository<Periodo, Long> {

    Optional<Set<Periodo>> findAllByStatusEnum (StatusEnum statusEnum);

    @Query(value =
            "SELECT DISTINCT periodo.* " +
            "FROM dia_cronograma " +
            "JOIN cronograma ON dia_cronograma.cronograma_id = cronograma.id " +
            "JOIN periodo ON cronograma.periodo_id = periodo.id " +
            "WHERE cronograma.curso_id IN :cursoIds",
            nativeQuery = true)
    List<Periodo> buscarTodosPorCursoIds(@Param("cursoIds") Set<Long> cursoIds);
}
