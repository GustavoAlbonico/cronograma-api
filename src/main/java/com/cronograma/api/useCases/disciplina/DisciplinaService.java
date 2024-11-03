package com.cronograma.api.useCases.disciplina;

import com.cronograma.api.entitys.*;
import com.cronograma.api.entitys.enums.BooleanEnum;
import com.cronograma.api.entitys.enums.StatusEnum;
import com.cronograma.api.exceptions.CursoException;
import com.cronograma.api.exceptions.DisciplinaException;
import com.cronograma.api.exceptions.EventoException;
import com.cronograma.api.useCases.disciplina.domains.DisciplinaRequestDom;
import com.cronograma.api.useCases.disciplina.domains.DisciplinaResponseDom;
import com.cronograma.api.useCases.disciplina.implement.mappers.DisciplinaMapper;
import com.cronograma.api.useCases.disciplina.implement.mappers.DisciplinaPaginacaoMapper;
import com.cronograma.api.useCases.disciplina.implement.repositorys.DisciplinaCursoRepository;
import com.cronograma.api.useCases.disciplina.implement.repositorys.DisciplinaFaseRepository;
import com.cronograma.api.useCases.disciplina.implement.repositorys.DisciplinaProfessorRepository;
import com.cronograma.api.useCases.disciplina.implement.repositorys.DisciplinaRepository;
import com.cronograma.api.useCases.usuario.UsuarioService;
import com.cronograma.api.utils.paginacao.PaginacaoRequestUtil;
import com.cronograma.api.utils.paginacao.PaginacaoResponseUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private final DisciplinaPaginacaoMapper disciplinaPaginacaoMapper;

    private final UsuarioService usuarioService;

    @Transactional(readOnly = true)
    public DisciplinaResponseDom carregarDisciplinaPorId(Long id){
        Disciplina disciplinaEncontrada = disciplinaRepository.findById(id)
                .orElseThrow(() -> new DisciplinaException("Nenhuma disciplina encontrada!"));

        validarUsuarioPertenceCurso(disciplinaEncontrada.getCurso().getId());
        return disciplinaMapper.disciplinaParaDisciplinaResponseDom(disciplinaEncontrada);
    }

    @Transactional(readOnly = true)
    public PaginacaoResponseUtil<List<DisciplinaResponseDom>> carregarDisciplina(Long cursoId, Long faseId, PaginacaoRequestUtil paginacaoRequestUtil){
        validarUsuarioPertenceCurso(cursoId);
        Page<Disciplina> disciplinasEncontradas =
                disciplinaRepository.findAllByCursoIdAndFaseId(cursoId,faseId,paginacaoRequestUtil.getPageRequest(List.of("statusEnum","nome")));
        return disciplinaPaginacaoMapper.pageDisciplinaParaPaginacaoResponseUtilDisciplinaResponseDom(disciplinasEncontradas,disciplinaMapper);
    }

    public void criarDisciplina(DisciplinaRequestDom disciplinaRequestDom){
        validarCampos(disciplinaRequestDom);

        Curso cursoEncontrado = disciplinaCursoRepository.findById(disciplinaRequestDom.getCursoId())
                .orElseThrow(() -> new DisciplinaException("Nenhum curso encontrado!"));

        validarUsuarioPertenceCurso(cursoEncontrado.getId());

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
        validarUsuarioPertenceCurso(disciplinaEncontrada.getCurso().getId());

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

    public void inativarDisciplina(Long id){
        Disciplina disciplinaEncontrada = disciplinaRepository.findById(id)
                .orElseThrow(() -> new DisciplinaException("Nenhuma disciplina encontrada!"));

        validarUsuarioPertenceCurso(disciplinaEncontrada.getCurso().getId());

        if (disciplinaEncontrada.getStatusEnum().equals(StatusEnum.INATIVO)){
            throw new CursoException("A disciplina já está Inativada");
        }

        disciplinaEncontrada.setStatusEnum(StatusEnum.INATIVO);
        disciplinaRepository.save(disciplinaEncontrada);
    }

    public void ativarDisciplina(Long id){
        Disciplina disciplinaEncontrada = disciplinaRepository.findById(id)
                .orElseThrow(() -> new DisciplinaException("Nenhuma disciplina encontrada!"));

        validarUsuarioPertenceCurso(disciplinaEncontrada.getCurso().getId());

        if (disciplinaEncontrada.getStatusEnum().equals(StatusEnum.ATIVO)){
            throw new CursoException("A disciplina já está Ativa");
        } else {
            disciplinaEncontrada.setStatusEnum(StatusEnum.ATIVO);
            disciplinaRepository.save(disciplinaEncontrada);
        }
    }

    private void validarCampos(DisciplinaRequestDom disciplina){
        List<String> errorMessages =  new ArrayList<>();

        if(disciplina.getNome() == null || disciplina.getNome().isBlank()){
            errorMessages.add("Nome é um campo obrigatório!");
        }

        if(disciplina.getCargaHoraria() == null){
            errorMessages.add("Carga Horária é um campo obrigatório!");
        } else if (disciplina.getCargaHoraria() < 1.0) {
            errorMessages.add("Carga Horária inválida!");
        }

        if(disciplina.getCargaHorariaDiaria() == null){
            errorMessages.add("Carga Horária Diária é um campo obrigatório!");
        } else if (disciplina.getCargaHorariaDiaria() < 1.0 || disciplina.getCargaHorariaDiaria() > 12.0) {
            errorMessages.add("Carga Horária Diária inválida!");
        }

        if(disciplina.getExtensaoBooleanEnum() == null){
            errorMessages.add("Extensão é um campo obrigatório!");
        } else if (
          Arrays.stream(BooleanEnum.values()).noneMatch(booleanEnum ->
              booleanEnum.equals(disciplina.getExtensaoBooleanEnum()))
        ){
            errorMessages.add("Extensão inválida!");
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

    private void validarUsuarioPertenceCurso(Long cursoId){
        Usuario usuario = usuarioService.buscarUsuarioAutenticado();

        if(
                usuario.getCoordenador() != null &&
                usuario.getNiveisAcesso().stream().noneMatch(nivelAcesso -> nivelAcesso.getRankingAcesso() < 2)
        ){
            if(usuario.getCoordenador().getCursos().stream().noneMatch(curso -> curso.getId().equals(cursoId))){
                throw new DisciplinaException("Você não possui acesso a este curso!");
            }
        }
    }
}
