package com.cronograma.api.useCases.usuario.implement.repositorys;

import com.cronograma.api.entitys.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
}
