package com.cronograma.api.useCases.diaCronograma.implement.repositorys;

import com.cronograma.api.entitys.DiaCronograma;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DiaCronogramaRepository extends JpaRepository<DiaCronograma, Long> {
}
