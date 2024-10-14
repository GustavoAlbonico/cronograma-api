package com.cronograma.api.useCases.curso;

import com.cronograma.api.entitys.*;
import com.cronograma.api.entitys.enums.StatusEnum;
import com.cronograma.api.exceptions.CursoException;
import com.cronograma.api.useCases.curso.domains.*;
import com.cronograma.api.useCases.curso.implement.mappers.CursoMapper;
import com.cronograma.api.useCases.curso.implement.mappers.CursoPorPeriodoMapper;
import com.cronograma.api.useCases.curso.implement.mappers.CursoPorUsuarioMapper;
import com.cronograma.api.useCases.curso.implement.repositorys.*;
import com.cronograma.api.useCases.usuario.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class CursoService {

    private final CursoRepository cursoRepository;
    private final CursoCoordenadorRepository cursoCoordenadorRepository;
    private final CursoFaseRepository cursoFaseRepository;
    private final CursoCronogramaRepository cursoCronogramaRepository;

    private final CursoMapper cursoMapper;
    private final CursoPorPeriodoMapper cursoPorPeriodoMapper;
    private final CursoPorUsuarioMapper cursoPorUsuarioMapper;

    private final UsuarioService usuarioService;


    @Transactional(readOnly = true)
    public List<CursoResponseDom> carregarCursoAtivo(){
        List<Curso> cursosEncontrados = cursoRepository.findAllByStatusEnum(StatusEnum.ATIVO);

        return cursosEncontrados.stream()
                .map(cursoMapper::cursoParaCursoResponseDom)
                .sorted(Comparator.comparing(CursoResponseDom::getNome))
                .toList();
    }

    @Transactional(readOnly = true)
    public CursoResponseDom carregarCursoPorId(Long id){
        Curso cursoEncontrado = cursoRepository.findById(id)
                .orElseThrow(() -> new CursoException("Nenhum curso encontrado!"));

        return cursoMapper.cursoParaCursoResponseDom(cursoEncontrado);
    }

    @Transactional(readOnly = true)
    public List<CursoResponseDom> carregarCurso(){
        List<Curso> cursosEncontrados = cursoRepository.findAll();

        return cursosEncontrados.stream()
                .map(cursoMapper::cursoParaCursoResponseDom)
                .sorted(Comparator.comparing(CursoResponseDom::getStatusEnum)
                        .thenComparing(CursoResponseDom::getNome))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<CursoPorUsuarioResponseDom> carregarCursoPorUsuario(){
        Usuario usuario = usuarioService.buscarUsuarioAutenticado();

        List<Curso> cursosEncontrados;
        if (usuario.getNiveisAcesso().stream().anyMatch(nivelAcesso -> nivelAcesso.getRankingAcesso() < 2)){
            cursosEncontrados = cursoRepository.findAllByStatusEnum(StatusEnum.ATIVO);
        } else {
            List<Long> cursoIds = usuario.getCoordenador().getCursos().stream().map(Curso::getId).toList();
            cursosEncontrados = cursoRepository.findAllByIdInAndStatusEnum(cursoIds,StatusEnum.ATIVO);
        }

        return cursosEncontrados.stream()
                .map(cursoPorUsuarioMapper::cursoParaCursoPorUsuarioResponseDom)
                .sorted(Comparator.comparing(CursoPorUsuarioResponseDom::getNome))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<CursoPorPeriodoResponseDom> carregarCursoPorPeriodo(Long id){
        Usuario usuario = usuarioService.buscarUsuarioAutenticado();
        List<Cronograma> cronogramasEncontrados = cursoCronogramaRepository.findAllByPeriodoId(id);

        List<CursoPorPeriodoResponseDom> cursosPorPeriodoResponse = new ArrayList<>();

        for (Cronograma cronograma : cronogramasEncontrados){
            List<CursoPorPeriodoFaseResponseDom> fasesPorPeriodoResponse = new ArrayList<>();

            for (DiaCronograma diaCronograma: cronograma.getDiasCronograma()){
                if(fasesPorPeriodoResponse.stream().noneMatch(fase -> fase.getId().equals(diaCronograma.getFase().getId()))){
                    CursoPorPeriodoFaseResponseDom cursoPorPeriodoFaseResponseDom = new CursoPorPeriodoFaseResponseDom();
                    cursoPorPeriodoMapper.faseParaCursoPorPeriodoFaseResponseDom(diaCronograma.getFase(),cursoPorPeriodoFaseResponseDom);
                    fasesPorPeriodoResponse.add(cursoPorPeriodoFaseResponseDom);
                }
            }

            CursoPorPeriodoResponseDom cursoPorPeriodoResponseDom = new CursoPorPeriodoResponseDom();
            cursoPorPeriodoMapper.cursoParaCursoPorPeriodoResponseDom(cronograma.getCurso(),cursoPorPeriodoResponseDom,fasesPorPeriodoResponse);
            cursosPorPeriodoResponse.add(cursoPorPeriodoResponseDom);
        }
        
        if(usuario.getAluno() != null) {
            for (CursoPorPeriodoResponseDom cursosPorPeriodo : cursosPorPeriodoResponse) {
                if (cursosPorPeriodo.getId().equals(usuario.getAluno().getCurso().getId())){
                    cursosPorPeriodo.setPossuiCurso(true);
                    cursosPorPeriodo.setEditavel(false);

                    for (CursoPorPeriodoFaseResponseDom fasePorPeriodo : cursosPorPeriodo.getFases()){
                        if(usuario.getAluno().getFases().stream().anyMatch(fase -> fase.getId().equals(fasePorPeriodo.getId()))){
                            fasePorPeriodo.setPossuiFase(true);
                        }
                    }
                }
            }
        }

        if(usuario.getProfessor() != null){
            for (CursoPorPeriodoResponseDom cursosPorPeriodo : cursosPorPeriodoResponse) {
                if (
                    usuario.getProfessor().getDisciplinas().stream()
                    .anyMatch(disciplina -> disciplina.getCurso().getId().equals(cursosPorPeriodo.getId()))
                ){
                    cursosPorPeriodo.setPossuiCurso(true);
                    cursosPorPeriodo.setEditavel(false);

                    for (CursoPorPeriodoFaseResponseDom fasePorPeriodo : cursosPorPeriodo.getFases()){
                        if(
                            usuario.getProfessor().getDisciplinas().stream()
                                   .anyMatch(disciplina -> disciplina.getFase().getId().equals(fasePorPeriodo.getId()))
                        ){
                            fasePorPeriodo.setPossuiFase(true);
                        }
                    }
                }
            }
        }

        if(usuario.getCoordenador() != null){
            for (CursoPorPeriodoResponseDom cursosPorPeriodo : cursosPorPeriodoResponse) {
                if (usuario.getCoordenador().getCursos().stream().anyMatch(curso -> curso.getId().equals(cursosPorPeriodo.getId()))){
                    cursosPorPeriodo.setPossuiCurso(true);
                    cursosPorPeriodo.setEditavel(true);

                    for (CursoPorPeriodoFaseResponseDom fasePorPeriodo : cursosPorPeriodo.getFases()){
                        fasePorPeriodo.setPossuiFase(true);
                    }
                }
            }
        }

        if (usuario.getNiveisAcesso().stream().anyMatch(nivelAcesso -> nivelAcesso.getRankingAcesso() < 2)){
            for (CursoPorPeriodoResponseDom cursosPorPeriodo : cursosPorPeriodoResponse) {
                    cursosPorPeriodo.setPossuiCurso(true);
                    cursosPorPeriodo.setEditavel(true);

                    for (CursoPorPeriodoFaseResponseDom fasePorPeriodo : cursosPorPeriodo.getFases()){
                        fasePorPeriodo.setPossuiFase(true);
                    }
            }
        }


        return cursosPorPeriodoResponse.stream()
                .filter(CursoPorPeriodoResponseDom::isPossuiCurso)
                .peek(cursosPorPeriodo -> {
                    cursosPorPeriodo.getFases().removeIf(fasePorPeriodo -> !fasePorPeriodo.isPossuiFase());

                    List<CursoPorPeriodoFaseResponseDom> fasesOrdenadas = cursosPorPeriodo.getFases().stream()
                            .sorted(Comparator.comparing(CursoPorPeriodoFaseResponseDom::getNumero))
                            .toList();

                    cursosPorPeriodo.setFases(fasesOrdenadas);
                })
                .sorted(Comparator.comparing(CursoPorPeriodoResponseDom::getNome))
                .toList();
    }

    public void criarCurso(CursoRequestDom cursoRequestDom){
        validarCampos(cursoRequestDom);

        List<Fase> fasesEncontradas = cursoFaseRepository.findAllById(cursoRequestDom.getFaseIds());

        if(fasesEncontradas.isEmpty()){
            throw new CursoException("Nenhuma Fase encontrada!");
        }
        if(fasesEncontradas.size() < cursoRequestDom.getFaseIds().size()){
            throw new CursoException("Uma ou mais das Fases informadas não foram encontradas!");
        }

        Coordenador coordenadorEncontrado = null;

        if (cursoRequestDom.getCoordenadorId() != null){
            coordenadorEncontrado  = cursoCoordenadorRepository.findById(cursoRequestDom.getCoordenadorId())
                    .orElseThrow(() -> new CursoException("Nenhum coordenador encontrado!"));
        }

       final Curso curso = new Curso();
       cursoMapper.cursoRequestDomParaCurso(cursoRequestDom,curso,fasesEncontradas,coordenadorEncontrado);
       cursoRepository.save(curso);
    }

    public void editarCurso(Long id, CursoRequestDom cursoRequestDom){
        validarCampos(cursoRequestDom);

        Curso cursoEncontrado = cursoRepository.findById(id)
                .orElseThrow(() -> new CursoException("Nenhum curso encontrado!"));

        List<Fase> fasesEncontradas = cursoFaseRepository.findAllById(cursoRequestDom.getFaseIds());

        if(fasesEncontradas.isEmpty()){
            throw new CursoException("Nenhuma Fase encontrada!");
        }
        if(fasesEncontradas.size() < cursoRequestDom.getFaseIds().size()){
            throw new CursoException("Uma ou mais das Fases informadas não foram encontradas!");
        }

        Coordenador coordenadorEncontrado = null;

        if (cursoRequestDom.getCoordenadorId() != null){
            coordenadorEncontrado  = cursoCoordenadorRepository.findById(cursoRequestDom.getCoordenadorId())
                    .orElseThrow(() -> new CursoException("Nenhum coordenador encontrado!"));
        }

        cursoMapper.cursoRequestDomParaCurso(cursoRequestDom,cursoEncontrado,fasesEncontradas,coordenadorEncontrado);
        cursoRepository.save(cursoEncontrado);
    }

    public void inativarCurso(Long id){
        Curso cursoEncontrado = cursoRepository.findById(id)
                .orElseThrow(() -> new CursoException("Nenhum curso encontrado!"));

        if (cursoEncontrado.getStatusEnum().equals(StatusEnum.INATIVO)){
            throw new CursoException("O curso já está Inativado");
        }
        if(!cursoEncontrado.getAlunos().isEmpty()){
            throw new CursoException("O curso está sendo utilizado em alunos");
        }

        if(!cursoEncontrado.getDisciplinas().isEmpty()){
            throw new CursoException("O curso está sendo utilizado em disciplinas");
        }

        cursoEncontrado.setStatusEnum(StatusEnum.INATIVO);
        cursoRepository.save(cursoEncontrado);
    }

    public void ativarCurso(Long id){
        Curso cursoEncontrado = cursoRepository.findById(id)
                .orElseThrow(() -> new CursoException("Nenhum curso encontrado!"));

        if (cursoEncontrado.getStatusEnum().equals(StatusEnum.ATIVO)){
            throw new CursoException("O curso já está Ativo");
        } else {
            cursoEncontrado.setStatusEnum(StatusEnum.ATIVO);
            cursoRepository.save(cursoEncontrado);
        }
    }



    private void validarCampos(CursoRequestDom curso){
        List<String> errorMessages =  new ArrayList<>();

        if(curso.getNome() == null || curso.getNome().isBlank()){
            errorMessages.add("Nome é um campo obrigatório!");
        }

        if(curso.getSigla() == null || curso.getSigla().isBlank()){
            errorMessages.add("Sigla é um campo obrigatório!");
        } else if (curso.getSigla().length() > 6) {
            errorMessages.add("Sigla inválida, tamanho máximo 6 caracteres!");
        }

        if (curso.getFaseIds() == null || curso.getFaseIds().isEmpty()){
            errorMessages.add("Fase é um campo obrigatório!");
        }

        if(!errorMessages.isEmpty()){
            throw new CursoException(errorMessages);
        }
    }
}
