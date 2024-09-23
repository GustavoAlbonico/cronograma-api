package com.cronograma.api.useCases.fase.implement.repositorys;

import com.cronograma.api.entitys.Fase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.stereotype.Repository;

@Repository
public interface FaseRepository extends JpaRepository<Fase, Long> {
    Optional<Fase> findByNumero(Integer numero);

    @Query(value =
            "SELECT COUNT(curso_fase.fase_id) " +
            "FROM curso_fase " +
            "WHERE curso_fase.fase_id = :faseId"
    ,nativeQuery = true)
    Long buscarQuantidadeCursoFasePorFaseId(@Param("faseId") Long faseId);

//    @Query(value =
//            "SELECT COUNT(aluno_fase.fase_id) " +
//            "FROM aluno_fase " +
//            "WHERE aluno_fase.fase_id = :faseId"
//            ,nativeQuery = true)
//    Long buscarQuantidadeAlunoFasePorFaseId(@Param("faseId") Long faseId);
}
