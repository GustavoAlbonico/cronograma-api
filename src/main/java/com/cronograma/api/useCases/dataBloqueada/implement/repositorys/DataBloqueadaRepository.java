package com.cronograma.api.useCases.dataBloqueada.implement.repositorys;

import com.cronograma.api.entitys.DataBloqueada;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DataBloqueadaRepository extends JpaRepository<DataBloqueada, Long> {
}
