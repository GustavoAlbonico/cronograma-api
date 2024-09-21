package com.cronograma.api.useCases.evento.implement.mappers;

import com.cronograma.api.entitys.Curso;
import com.cronograma.api.entitys.Evento;
import com.cronograma.api.entitys.Periodo;
import com.cronograma.api.entitys.Usuario;
import com.cronograma.api.exceptions.EventoException;
import com.cronograma.api.useCases.evento.domains.EventoRequestDom;
import com.cronograma.api.useCases.evento.implement.repositorys.EventoCursoRepository;
import com.cronograma.api.useCases.evento.implement.repositorys.EventoPeriodoRepository;
import com.cronograma.api.useCases.evento.implement.repositorys.EventoUsuarioRepository;
import org.mapstruct.*;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface EventoMapper{

    @Mapping(target = "data" , ignore = true)
    @Mapping(target = "curso" , ignore = true)
    @Mapping(target = "periodo",ignore = true)
    @Mapping(target = "usuario",ignore = true)
    void eventoRequestDomParaEvento(
            EventoRequestDom eventoRequestDom,
            @MappingTarget Evento evento,
            @Context EventoCursoRepository eventoCursoRepository,
            @Context EventoPeriodoRepository eventoPeriodoRepository,
            @Context EventoUsuarioRepository eventoUsuarioRepository);

    @AfterMapping
    default void afterEventoRequestDomParaEvento(
            EventoRequestDom eventoRequestDom,
            @MappingTarget Evento evento,
            @Context EventoCursoRepository eventoCursoRepository,
            @Context EventoPeriodoRepository eventoPeriodoRepository,
            @Context EventoUsuarioRepository eventoUsuarioRepository
    ){
        final Curso cursoEncontrado = eventoCursoRepository.findById(eventoRequestDom.getCursoId())
                .orElseThrow(() -> new EventoException("Nenhum curso encontrado!"));
        final Periodo periodoEncontrado = eventoPeriodoRepository.findById(eventoRequestDom.getPeriodoId())
                .orElseThrow(() -> new EventoException("Nenhum periodo encontrado!"));
        final Usuario usuarioEncontrado = eventoUsuarioRepository.findById(eventoRequestDom.getUsuarioId())
                .orElseThrow(() -> new EventoException("Nenhum usuario encontrado!"));

        evento.setCurso(cursoEncontrado);
        evento.setPeriodo(periodoEncontrado);
        evento.setUsuario(usuarioEncontrado);
    }

}
