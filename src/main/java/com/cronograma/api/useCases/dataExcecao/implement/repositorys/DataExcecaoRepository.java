package com.cronograma.api.useCases.dataExcecao.implement.repositorys;

import com.cronograma.api.entitys.DataExcecao;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DataExcecaoRepository extends JpaRepository<DataExcecao, Long> {
}
