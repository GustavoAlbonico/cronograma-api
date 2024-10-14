package com.cronograma.api.useCases.professor.implement.repositorys;

import com.cronograma.api.entitys.NivelAcesso;
import com.cronograma.api.useCases.nivelAcesso.implement.repositorys.NivelAcessoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProfessorNivelAcessoRepository extends NivelAcessoRepository {
    Optional<NivelAcesso> findByNome(String nome);
}
