package com.cronograma.api.useCases.fase.implement.repositorys;

import com.cronograma.api.useCases.disciplina.implement.repositorys.DisciplinaRepository;

public interface FaseDisciplinaRepository extends DisciplinaRepository {
    Long countByFaseId(Long faseId);
}
