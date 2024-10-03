package com.cronograma.api.useCases.curso.implement.mappers;

import com.cronograma.api.entitys.Coordenador;
import com.cronograma.api.entitys.Curso;
import com.cronograma.api.entitys.Fase;
import com.cronograma.api.useCases.coordenador.domains.CoordenadorResponseDom;
import com.cronograma.api.useCases.curso.domains.CursoCoordenadorResponseDom;
import com.cronograma.api.useCases.curso.domains.CursoRequestDom;
import com.cronograma.api.useCases.curso.domains.CursoResponseDom;
import org.mapstruct.*;

import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface CursoMapper {

    void cursoRequestDomParaCurso(
            CursoRequestDom cursoRequestDom,
            @MappingTarget Curso curso,
            @Context List<Fase> fasesEncontradas,
            @Context Coordenador coordenadorEncontrado
    );

   @AfterMapping
   default void afterCursoRequestDomParaCurso(
           @MappingTarget Curso curso,
           @Context List<Fase> fasesEncontradas,
           @Context Coordenador coordenadorEncontrado
   ){
       curso.setCoordenador(coordenadorEncontrado);
       curso.setFases(new HashSet<>(fasesEncontradas));
   }

    @BeforeMapping
    default void beforeCursoParaCursoResponseDom(Curso curso) {
       if (curso != null){
           curso.setFases(
                   curso.getFases().stream()
                           .sorted(Comparator.comparing(Fase::getNumero))
                           .collect(Collectors.toCollection(LinkedHashSet::new))
           );
       }
    }

    @Mapping(source = "coordenador",target = "coordenador", qualifiedByName = "coordenadorParaCursoCoordenadorResponseDom")
    CursoResponseDom cursoParaCursoResponseDom(Curso curso);


    @Named("coordenadorParaCursoCoordenadorResponseDom")
    default CursoCoordenadorResponseDom coordenadorParaCursoCoordenadorResponseDom(Coordenador coordenador){
        CursoCoordenadorResponseDom cursoCoordenadorResponseDom = new CursoCoordenadorResponseDom();
        cursoCoordenadorResponseDom.setId(coordenador.getId());
        cursoCoordenadorResponseDom.setCpf(coordenador.getUsuario().getCpf());
        cursoCoordenadorResponseDom.setNome(coordenador.getUsuario().getNome());
        cursoCoordenadorResponseDom.setTelefone(coordenador.getTelefone());
        cursoCoordenadorResponseDom.setEmail(coordenador.getUsuario().getEmail());
        return cursoCoordenadorResponseDom;
   }
}
