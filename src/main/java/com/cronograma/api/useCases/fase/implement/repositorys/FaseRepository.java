package com.cronograma.api.useCases.fase.implement.repositorys;

import com.cronograma.api.entitys.Fase;
import com.cronograma.api.entitys.enums.StatusEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.stereotype.Repository;

@Repository
public interface FaseRepository extends JpaRepository<Fase, Long> {

    List<Fase> findAllByStatusEnum(StatusEnum statusEnum);

    @Query(value =
            "SELECT DISTINCT fase.* " +
            "FROM fase " +
            "JOIN curso_fase ON curso_fase.fase_id = fase.id " +
            "WHERE curso_fase.curso_id = :cursoId " +
            "AND fase.status_enum = :statusEnum"
            ,nativeQuery = true)
    List<Fase> buscaTodosPorStatusEnumPorCursoId(@Param("statusEnum") String statusEnum,@Param("cursoId") Long cursoId);

    boolean existsByNumero(Integer numero);

    @Query(value =
            "SELECT COUNT(curso_fase.fase_id) > 0 " +
            "FROM curso_fase " +
            "WHERE curso_fase.fase_id = :faseId"
    ,nativeQuery = true)
    boolean existeCursoFasePorFaseId(@Param("faseId") Long faseId);

    @Query(value =
        "SELECT COUNT(aluno_fase.fase_id) > 0 " +
        "FROM aluno_fase " +
        "WHERE aluno_fase.fase_id = :faseId"
        ,nativeQuery = true)
    boolean existeAlunoFasePorFaseId(@Param("faseId") Long faseId);
}
