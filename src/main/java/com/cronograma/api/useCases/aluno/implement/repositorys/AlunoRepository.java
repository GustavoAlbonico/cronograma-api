package com.cronograma.api.useCases.aluno.implement.repositorys;

import com.cronograma.api.entitys.Aluno;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AlunoRepository extends JpaRepository<Aluno, Long> {
}
