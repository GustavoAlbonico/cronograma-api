package com.cronograma.api.useCases.usuario.implement.repositorys;

import com.cronograma.api.entitys.Usuario;
import com.cronograma.api.entitys.enums.StatusEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByCpfAndStatusEnum(String cpf, StatusEnum statusEnum);
    boolean existsByCpf(String cpf);

}
