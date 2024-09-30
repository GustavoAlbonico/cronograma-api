package com.cronograma.api.useCases.professor.implement.repositorys;

import com.cronograma.api.entitys.Professor;
import com.cronograma.api.entitys.enums.StatusEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProfessorRepository extends JpaRepository<Professor, Long> {

    List<Professor> findAllByStatusEnum(StatusEnum statusEnum);
}
