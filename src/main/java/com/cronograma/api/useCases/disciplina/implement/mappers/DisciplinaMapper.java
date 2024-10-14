package com.cronograma.api.useCases.disciplina.implement.mappers;

import com.cronograma.api.entitys.Curso;
import com.cronograma.api.entitys.Disciplina;
import com.cronograma.api.entitys.Fase;
import com.cronograma.api.entitys.Professor;
import com.cronograma.api.useCases.disciplina.domains.DisciplinaProfessorResponseDom;
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

   @Mapping(target = "professor", qualifiedByName = "professorParaDisciplinaProfessorResponseDom")
   DisciplinaResponseDom disciplinaParaDisciplinaResponseDom(Disciplina disciplina);

   @Named("professorParaDisciplinaProfessorResponseDom")
    default DisciplinaProfessorResponseDom professorParaDisciplinaProfessorResponseDom(Professor professor){
       if (professor != null){
           DisciplinaProfessorResponseDom disciplinaProfessorResponseDom = new DisciplinaProfessorResponseDom();
           disciplinaProfessorResponseDom.setId(professor.getId());
           disciplinaProfessorResponseDom.setNome(professor.getUsuario().getNome());
           return disciplinaProfessorResponseDom;
       }else {
           return null;
       }
   }
}
