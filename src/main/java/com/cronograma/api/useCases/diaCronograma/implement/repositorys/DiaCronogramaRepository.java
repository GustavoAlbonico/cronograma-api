package com.cronograma.api.useCases.diaCronograma.implement.repositorys;

import com.cronograma.api.entitys.DiaCronograma;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface DiaCronogramaRepository extends JpaRepository<DiaCronograma, Long> {

    @Query(value =
            "SELECT dia_cronograma.* " +
            "FROM dia_cronograma " +
            "JOIN disciplina ON dia_cronograma.disciplina_id = disciplina.id " +
            "WHERE disciplina.professor_id = :professorId " +
            "AND dia_cronograma.data = :data " +
            "GROUP BY dia_cronograma.id",
            nativeQuery = true)
    Optional<DiaCronograma> buscarPorProfessorIdPorData(@Param("professorId") Long professorId, @Param("data") LocalDate data);
}
