package com.cronograma.api.useCases.historico.implement.repositorys;

import com.cronograma.api.entitys.Historico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HistoricoRepository extends JpaRepository<Historico, Long> {
}
