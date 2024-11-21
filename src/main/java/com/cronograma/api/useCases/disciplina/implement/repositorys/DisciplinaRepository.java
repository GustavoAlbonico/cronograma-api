package com.cronograma.api.useCases.disciplina.implement.repositorys;

import com.cronograma.api.entitys.Disciplina;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;

@Repository
public interface DisciplinaRepository extends JpaRepository<Disciplina, Long> {
    Page<Disciplina> findAllByCursoIdAndFaseId(Long cursoId , Long faseId, PageRequest pageRequest);
}
