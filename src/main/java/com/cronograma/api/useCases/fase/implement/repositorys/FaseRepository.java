package com.cronograma.api.useCases.fase.implement.repositorys;

import com.cronograma.api.entitys.Fase;
import com.cronograma.api.entitys.enums.StatusEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
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
}
