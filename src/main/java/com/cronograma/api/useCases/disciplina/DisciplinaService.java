package com.cronograma.api.useCases.disciplina;

import com.cronograma.api.entitys.*;
import com.cronograma.api.entitys.enums.BooleanEnum;
import com.cronograma.api.exceptions.CursoException;
import com.cronograma.api.exceptions.DisciplinaException;
import com.cronograma.api.useCases.disciplina.domains.DisciplinaRequestDom;
import com.cronograma.api.useCases.disciplina.implement.mappers.DisciplinaMapper;
import com.cronograma.api.useCases.disciplina.implement.repositorys.DisciplinaCursoRepository;
import com.cronograma.api.useCases.disciplina.implement.repositorys.DisciplinaFaseRepository;
import com.cronograma.api.useCases.disciplina.implement.repositorys.DisciplinaProfessorRepository;
import com.cronograma.api.useCases.disciplina.implement.repositorys.DisciplinaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DisciplinaService {

    private final DisciplinaRepository disciplinaRepository;
    private final DisciplinaProfessorRepository disciplinaProfessorRepository;
    private final DisciplinaCursoRepository disciplinaCursoRepository;
    private final DisciplinaFaseRepository disciplinaFaseRepository;

    private final DisciplinaMapper disciplinaMapper;

    public void criarDisciplina(DisciplinaRequestDom disciplinaRequestDom){
        validarCampos(disciplinaRequestDom);

        Curso cursoEncontrado = disciplinaCursoRepository.findById(disciplinaRequestDom.getCursoId())
                .orElseThrow(() -> new DisciplinaException("Nenhum curso encontrado!"));

        Fase faseEncontrada = disciplinaFaseRepository.findById(disciplinaRequestDom.getFaseId())
                .orElseThrow(() -> new DisciplinaException("Nenhuma fase encontrada!"));

        Professor professorEncontrado = null;
        if (disciplinaRequestDom.getProfessorId() != null){
            professorEncontrado  = disciplinaProfessorRepository.findById(disciplinaRequestDom.getProfessorId())
                    .orElseThrow(() -> new DisciplinaException("Nenhum professor encontrado!"));
        }

        final Disciplina disciplina = new Disciplina();
        disciplinaMapper.disciplinaRequestDomParaDisciplina(
                disciplinaRequestDom,
                disciplina,
                cursoEncontrado,
                faseEncontrada,
                professorEncontrado
        );

        disciplinaRepository.save(disciplina);
    }

    public void editarDisciplina(Long id,DisciplinaRequestDom disciplinaRequestDom){
        Disciplina disciplinaEncontrada = disciplinaRepository.findById(id)
                .orElseThrow(() -> new DisciplinaException("Nenhuma disciplina encontrada!"));

        validarCampos(disciplinaRequestDom);

        Curso cursoEncontrado = disciplinaCursoRepository.findById(disciplinaRequestDom.getCursoId())
                .orElseThrow(() -> new DisciplinaException("Nenhum curso encontrado!"));

        Fase faseEncontrada = disciplinaFaseRepository.findById(disciplinaRequestDom.getFaseId())
                .orElseThrow(() -> new DisciplinaException("Nenhuma fase encontrada!"));

        Professor professorEncontrado = null;
        if (disciplinaRequestDom.getProfessorId() != null){
            professorEncontrado  = disciplinaProfessorRepository.findById(disciplinaRequestDom.getProfessorId())
                    .orElseThrow(() -> new DisciplinaException("Nenhum professor encontrado!"));
        }

        disciplinaMapper.disciplinaRequestDomParaDisciplina(
                disciplinaRequestDom,
                disciplinaEncontrada,
                cursoEncontrado,
                faseEncontrada,
                professorEncontrado
        );

        disciplinaRepository.save(disciplinaEncontrada);
    }

    private void validarCampos(DisciplinaRequestDom disciplina){
        List<String> errorMessages =  new ArrayList<>();

        if(disciplina.getNome() == null || disciplina.getNome().isBlank()){
            errorMessages.add("Nome é um campo obrigatório!");
        }

        if(disciplina.getCargaHoraria() == null){
            errorMessages.add("Carga Horaria é um campo obrigatório!");
        } else if (disciplina.getCargaHoraria() < 1.0) {
            errorMessages.add("Carga Horaria inválida!");
        }

        if(disciplina.getCargaHorariaDiaria() == null){
            errorMessages.add("Carga Horaria Diaria é um campo obrigatório!");
        } else if (disciplina.getCargaHorariaDiaria() < 1.0 || disciplina.getCargaHorariaDiaria() > 12.0) {
            errorMessages.add("Carga Horaria Diaria inválida!");
        }

        if(disciplina.getExtensaoBooleanEnum() == null){
            errorMessages.add("Extensao é um campo obrigatório!");
        } else if (
          Arrays.stream(BooleanEnum.values()).noneMatch(booleanEnum ->
              booleanEnum.equals(disciplina.getExtensaoBooleanEnum()))
        ){
            errorMessages.add("Extensao inválida!");
        }

        if(disciplina.getCorHexadecimal() == null || disciplina.getCorHexadecimal().isBlank()){
            errorMessages.add("Cor Hexadecimal é um campo obrigatório!");
        } else if (!disciplina.getCorHexadecimal().contains("#") || disciplina.getCorHexadecimal().length() > 15) {
            errorMessages.add("Cor Hexadecimal inválida!");
        }

        if (disciplina.getCursoId() == null){
            errorMessages.add("Curso é um campo obrigatório!");
        }

        if (disciplina.getFaseId() == null){
            errorMessages.add("Fase é um campo obrigatório!");
        }

        if(!errorMessages.isEmpty()){
            throw new DisciplinaException(errorMessages);
        }
    }
}
