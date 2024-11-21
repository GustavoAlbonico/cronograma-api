package com.cronograma.api.useCases.nivelAcesso.implement.repositorys;

import com.cronograma.api.entitys.NivelAcesso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NivelAcessoRepository extends JpaRepository<NivelAcesso, Long> {
}
