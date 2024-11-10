package com.cronograma.api.useCases.professor.implement.repositorys;

import com.cronograma.api.entitys.enums.DiaSemanaEnum;
import com.cronograma.api.useCases.diaCronograma.implement.repositorys.DiaCronogramaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProfessorDiaCronogramaRepository extends DiaCronogramaRepository {

    @Query(value =
            "SELECT DISTINCT dia_cronograma.dia_semana_enum " +
            "FROM dia_cronograma " +
            "JOIN cronograma ON cronograma.id = dia_cronograma.cronograma_id " +
            "JOIN periodo ON periodo.id  = cronograma.periodo_id " +
            "JOIN disciplina ON disciplina.id  = dia_cronograma.disciplina_id " +
            "WHERE periodo.status_enum  = 'ATIVO' " +
            "AND disciplina.professor_id = :professorId"
            ,
            nativeQuery = true)
    List<DiaSemanaEnum> buscarTodosDiasSemanaEnumPorCronogramaGeradoPorPeriodoAtivoPorProfessor(@Param("professorId") Long professorId);
}
