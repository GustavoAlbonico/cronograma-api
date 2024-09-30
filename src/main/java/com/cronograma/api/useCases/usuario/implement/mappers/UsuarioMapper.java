package com.cronograma.api.useCases.usuario.implement.mappers;

import com.cronograma.api.entitys.NivelAcesso;
import com.cronograma.api.entitys.Usuario;
import com.cronograma.api.useCases.usuario.domains.UsuarioRequestDom;
import org.mapstruct.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.Set;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface UsuarioMapper {

    @Mapping(target = "niveisAcesso", ignore = true)
    @Mapping(target = "senha", ignore = true)
    Usuario usuarioRequestDomParaUsuario (
            UsuarioRequestDom usuarioRequestDom,
            @Context Set<NivelAcesso> niveisAcesso,
            @Context PasswordEncoder passwordEncoder
    );

    @AfterMapping
    default void afterUsuarioRequestDomParaUsuario(
            UsuarioRequestDom usuarioRequestDom,
            @MappingTarget Usuario usuario,
            @Context Set<NivelAcesso> niveisAcesso,
            @Context PasswordEncoder passwordEncoder
    ){
        usuario.setSenha(passwordEncoder.encode(usuarioRequestDom.getSenha()));
        usuario.setNiveisAcesso(niveisAcesso);
    }

   @Mapping(target = "id", ignore = true)
   @Mapping(target = "senha", ignore = true)
   void usuarioRequestDomParaUsuarioEncontrado (
           UsuarioRequestDom usuarioRequestDom,
           @MappingTarget Usuario usuario
   );
}
