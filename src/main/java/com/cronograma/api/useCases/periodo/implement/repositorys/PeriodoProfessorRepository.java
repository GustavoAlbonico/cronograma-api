package com.cronograma.api.useCases.periodo.implement.repositorys;

import com.cronograma.api.useCases.professor.implement.repositorys.ProfessorRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface PeriodoProfessorRepository extends ProfessorRepository {

    @Modifying
    @Query(value = "DELETE FROM professor_dia_semana_disponivel",nativeQuery = true)
    void excluirProfessorDiasSemanaDisponiveis();
}
