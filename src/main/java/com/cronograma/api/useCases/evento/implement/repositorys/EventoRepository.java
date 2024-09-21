package com.cronograma.api.useCases.evento.implement.repositorys;

import com.cronograma.api.entitys.Evento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventoRepository extends JpaRepository<Evento, Long> {
}
