package com.cronograma.api.useCases.coordenador.implement.repositorys;

import com.cronograma.api.useCases.curso.implement.repositorys.CursoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CoordenadorCursoRepository extends CursoRepository {
    boolean existsByCoordenadorId(Long coordenadorId);
}
