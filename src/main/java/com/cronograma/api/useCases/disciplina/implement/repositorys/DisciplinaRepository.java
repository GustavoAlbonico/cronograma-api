package com.cronograma.api.useCases.disciplina.implement.repositorys;

import com.cronograma.api.entitys.DiaCronograma;
import com.cronograma.api.entitys.Disciplina;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DisciplinaRepository extends JpaRepository<Disciplina, Long> {
    List<Disciplina> findAllByCursoIdAndFaseId(Long cursoId, Long faseId);
}
