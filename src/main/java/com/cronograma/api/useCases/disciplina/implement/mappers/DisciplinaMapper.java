package com.cronograma.api.useCases.disciplina.implement.mappers;

import com.cronograma.api.entitys.Curso;
import com.cronograma.api.entitys.Disciplina;
import com.cronograma.api.entitys.Fase;
import com.cronograma.api.entitys.Professor;
import com.cronograma.api.useCases.disciplina.domains.DisciplinaRequestDom;
import com.cronograma.api.useCases.disciplina.domains.DisciplinaResponseDom;
import org.mapstruct.*;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface DisciplinaMapper {

   @Mapping(target = "curso", ignore = true)
   @Mapping(target = "fase", ignore = true)
   @Mapping(target = "professor", ignore = true)
   void disciplinaRequestDomParaDisciplina(
           DisciplinaRequestDom disciplinaRequestDom,
           @MappingTarget Disciplina disciplina,
           @Context Curso cursoEncontrado,
           @Context Fase faseEncontrada,
           @Context Professor professorEncontrado
   );

   @AfterMapping
   default void afterDisciplinaRequestDomParaDisciplina(
           @MappingTarget Disciplina disciplina,
           @Context Curso cursoEncontrado,
           @Context Fase faseEncontrada,
           @Context Professor professorEncontrado
   ){
       disciplina.setCurso(cursoEncontrado);
       disciplina.setFase(faseEncontrada);
       disciplina.setProfessor(professorEncontrado);
   }

   DisciplinaResponseDom disciplinaParaDisciplinaResponseDom(Disciplina disciplina);
}
