package com.cronograma.api.useCases.usuario.implement.repositorys;

import com.cronograma.api.entitys.NivelAcesso;
import com.cronograma.api.useCases.nivelAcesso.implement.repositorys.NivelAcessoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;
@Repository
public interface UsuarioNivelAcessoRepository extends NivelAcessoRepository {

    Optional<Set<NivelAcesso>> findByNome(String nome);
}
