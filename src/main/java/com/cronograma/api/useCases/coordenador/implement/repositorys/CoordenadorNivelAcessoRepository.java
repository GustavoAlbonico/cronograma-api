package com.cronograma.api.useCases.coordenador.implement.repositorys;

import com.cronograma.api.entitys.NivelAcesso;
import com.cronograma.api.useCases.nivelAcesso.implement.repositorys.NivelAcessoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
public interface CoordenadorNivelAcessoRepository extends NivelAcessoRepository {
    Optional<NivelAcesso> findByNome(String nome);
}
