package com.cronograma.api.useCases.evento.implement.mappers;

import com.cronograma.api.entitys.Curso;
import com.cronograma.api.entitys.Evento;
import com.cronograma.api.entitys.Usuario;
import com.cronograma.api.exceptions.EventoException;
import com.cronograma.api.useCases.evento.domains.EventoRequestDom;
import com.cronograma.api.useCases.evento.domains.EventoResponseDom;
import com.cronograma.api.useCases.evento.implement.repositorys.EventoCursoRepository;
import com.cronograma.api.useCases.evento.implement.repositorys.EventoUsuarioRepository;
import org.mapstruct.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface EventoMapper{

    @Mapping(target = "data" , ignore = true)
    @Mapping(target = "curso" , ignore = true)
    @Mapping(target = "usuario",ignore = true)
    void eventoRequestDomParaEvento(
            EventoRequestDom eventoRequestDom,
            @MappingTarget Evento evento,
            @Context EventoCursoRepository eventoCursoRepository,
            @Context EventoUsuarioRepository eventoUsuarioRepository);

    @AfterMapping
    default void afterEventoRequestDomParaEvento(
            EventoRequestDom eventoRequestDom,
            @MappingTarget Evento evento,
            @Context EventoCursoRepository eventoCursoRepository,
            @Context EventoUsuarioRepository eventoUsuarioRepository
    ){
        final Curso cursoEncontrado = eventoCursoRepository.findById(eventoRequestDom.getCursoId())
                .orElseThrow(() -> new EventoException("Nenhum curso encontrado!"));
        final Usuario usuarioEncontrado = eventoUsuarioRepository.findById(eventoRequestDom.getUsuarioId())
                .orElseThrow(() -> new EventoException("Nenhum usuario encontrado!"));

        evento.setCurso(cursoEncontrado);
        evento.setUsuario(usuarioEncontrado);
    }

    @Mapping(source = "curso", target = "siglaCurso", qualifiedByName = "buscarSiglaCurso")
    @Mapping(source = "curso", target = "cursoId", qualifiedByName = "buscarCursoId")
    @Mapping(source = "data",target = "data", qualifiedByName = "formatarData")
    EventoResponseDom eventoParaEventoResponseDom(Evento evento);

    @Named("buscarSiglaCurso")
    default String buscarSiglaCurso(Curso curso){
        return curso.getSigla();
    }
    @Named("buscarCursoId")
    default Long buscarCursoId(Curso curso){
        return curso.getId();
    }

    @Named("formatarData")
    default String formatarData(LocalDateTime data){
        DateTimeFormatter dataFormatoPtBrComHora = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        return dataFormatoPtBrComHora.format(data);
    }

}
