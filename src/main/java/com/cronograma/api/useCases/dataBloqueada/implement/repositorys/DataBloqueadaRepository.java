package com.cronograma.api.useCases.dataBloqueada.implement.repositorys;

import com.cronograma.api.entitys.DataBloqueada;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DataBloqueadaRepository extends JpaRepository<DataBloqueada, Long> {
}
