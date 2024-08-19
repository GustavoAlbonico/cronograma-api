package com.cronograma.api.useCases.disciplina.implement.repositorys;

import com.cronograma.api.entitys.Disciplina;
import com.cronograma.api.entitys.Professor;
import com.cronograma.api.entitys.enums.DiaSemanaEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.Set;

public interface DisciplinaRepository extends JpaRepository<Disciplina, Long> {

    Optional<Set<Disciplina>> findByCursoId(Long id);
    Optional<Set<Disciplina>> findByFaseId(Long id);

    @Query(value =
            "SELECT diaSemanaDis.dia_semana_enum " +
            "FROM disciplina dis " +
            "JOIN professor prof ON dis.professor_id = prof.id " +
            "JOIN dia_semana_disponivel diaSemanaDis on diaSemanaDis.professor_id = prof.id " +
            "WHERE dis.curso_id = :cursoId " +
            "AND dis.fase_id = :faseId " +
            "GROUP BY diaSemanaDis.dia_semana_enum",
          nativeQuery = true)
    Optional<Set<DiaSemanaEnum>> buscarDiasDaSemanaDisponiveisRelacionadosProfessoresPorCursoIdPorFaseId(@Param("cursoId") Long cursoId, @Param("faseId") Long faseId);
}
