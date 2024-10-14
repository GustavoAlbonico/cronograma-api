package com.cronograma.api.useCases.cronograma.implement.repositorys;

import com.cronograma.api.entitys.Professor;
import com.cronograma.api.useCases.professor.implement.repositorys.ProfessorRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CronogramaProfessorRepository extends ProfessorRepository {

    @Query(value =
            "SELECT DISTINCT professor.* " +
            "FROM disciplina " +
            "JOIN professor ON disciplina.professor_id = professor.id " +
            "WHERE disciplina.curso_id = :cursoId " +
            "AND disciplina.status_enum = :statusEnum " +
            "AND professor.status_enum = :statusEnum" ,
            nativeQuery = true)
    List<Professor> buscarTodosPorStatusEnumPorCursoId(@Param("statusEnum") String statusEnum, @Param("cursoId") Long cursoId);
}
