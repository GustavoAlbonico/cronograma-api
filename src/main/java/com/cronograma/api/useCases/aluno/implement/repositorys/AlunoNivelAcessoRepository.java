package com.cronograma.api.useCases.aluno.implement.repositorys;

import com.cronograma.api.entitys.NivelAcesso;
import com.cronograma.api.useCases.nivelAcesso.implement.repositorys.NivelAcessoRepository;

import java.util.Optional;
import java.util.Set;

public interface AlunoNivelAcessoRepository extends NivelAcessoRepository {

    Optional<Set<NivelAcesso>> findByNome(String nome);
}
