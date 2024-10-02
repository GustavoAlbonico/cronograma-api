package com.cronograma.api.useCases.coordenador.implement.mappers;

import com.cronograma.api.entitys.Coordenador;
import com.cronograma.api.entitys.Usuario;
import com.cronograma.api.useCases.coordenador.domains.CoordenadorRequestDom;
import com.cronograma.api.useCases.coordenador.domains.CoordenadorResponseDom;
import com.cronograma.api.useCases.coordenador.domains.CoordenadorUsuarioRequestDom;
import com.cronograma.api.utils.regex.RegexUtil;
import org.mapstruct.*;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface CoordenadorMapper {

    @Mapping(source = "cpf", target = "cpf", qualifiedByName = "cpfRetornarNumeros")
    CoordenadorUsuarioRequestDom coordenadorRequestDomParaCoordenadorUsuarioRequestDom(CoordenadorRequestDom coordenadorRequestDom);

    @Named("cpfRetornarNumeros")
    default String cpfRetornarNumeros(String cpf){
        return RegexUtil.retornarNumeros(cpf);
    }

    @Mapping(target = "telefone", ignore = true)
    @Mapping(target = "usuario", ignore = true)
    Coordenador coordenadorRequestDomParaCoordenador(
            CoordenadorRequestDom coordenadorRequestDom,
            @Context Usuario usuario);

    @AfterMapping
    default void afterCoordenadorRequestDomParaCoordenador(
            CoordenadorRequestDom coordenadorRequestDom,
            @MappingTarget Coordenador coordenador,
            @Context Usuario usuario
    ){
        coordenador.setTelefone(RegexUtil.retornarNumeros(coordenadorRequestDom.getTelefone()));
        coordenador.setUsuario(usuario);
    }

    @Mapping(source = "usuario",target = "nome", qualifiedByName = "buscarNome")
    @Mapping(source = "usuario",target = "cpf", qualifiedByName = "buscarCpf")
    @Mapping(source = "usuario",target = "email", qualifiedByName = "buscarEmail")
    CoordenadorResponseDom coordenadorParaCoordenadorResponseDom(Coordenador coordenador);

    @Named("buscarNome")
    default String buscarNome(Usuario usuario){
        return usuario.getNome();
    }
    @Named("buscarEmail")
    default String buscarEmail(Usuario usuario){
        return usuario.getEmail();
    }

    @Named("buscarCpf")
    default String buscarCpf(Usuario usuario){
        return usuario.getCpf();
    }

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "telefone",qualifiedByName = "editarTelefone")
    @Mapping(target = "usuario", ignore = true)
    @Mapping(target = "cursos", ignore = true)
    void coordenadorRequestDomParaCoordenadorEncontrado(
            CoordenadorRequestDom coordenadorRequestDom,
            @MappingTarget Coordenador coordenador
    );

    @Named("editarTelefone")
    default String editarTelefone (String telefone){
        return RegexUtil.retornarNumeros(telefone);
    }
}
