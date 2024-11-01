package com.cronograma.api.useCases.aluno.implement.repositorys;

import com.cronograma.api.entitys.Aluno;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AlunoRepository extends JpaRepository<Aluno, Long> {

    @Query(value =
            "SELECT DISTINCT aluno.*, usuario.nome " +
            "FROM aluno " +
            "JOIN aluno_fase ON aluno_fase.aluno_id = aluno.id " +
            "JOIN usuario ON aluno.usuario_id = usuario.id " +
            "WHERE aluno.curso_id = :cursoId " +
            "AND aluno_fase.fase_id = :faseId " +
            "ORDER BY usuario.nome",
            nativeQuery = true)
    Page<Aluno> buscarTodosPorCursoPorFase(@Param("cursoId") Long cursoId, @Param("faseId") Long faseId, PageRequest pageRequest);
}
