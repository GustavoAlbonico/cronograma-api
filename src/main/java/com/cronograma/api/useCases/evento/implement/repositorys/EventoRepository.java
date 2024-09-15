package com.cronograma.api.useCases.evento.implement.repositorys;

import com.cronograma.api.entitys.Evento;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventoRepository extends JpaRepository<Evento, Long> {
}
