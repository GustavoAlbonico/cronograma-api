package com.cronograma.api.useCases.curso.implement.repositorys;

import com.cronograma.api.entitys.Curso;
import com.cronograma.api.entitys.enums.StatusEnum;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public interface CursoRepository extends JpaRepository<Curso, Long> {
    List<Curso> findAllByStatusEnum(StatusEnum statusEnum);

    List<Curso> findAllByIdInAndStatusEnum(List<Long> ids, StatusEnum statusEnum);
}
