package com.cronograma.api.useCases.coordenador.implement.repositorys;

import com.cronograma.api.entitys.Coordenador;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CoordenadorRepository extends JpaRepository<Coordenador, Long> {

}
