package com.cronograma.api.useCases.curso;

import com.cronograma.api.entitys.*;
import com.cronograma.api.entitys.enums.StatusEnum;
import com.cronograma.api.exceptions.CursoException;
import com.cronograma.api.useCases.curso.domains.CursoRequestDom;
import com.cronograma.api.useCases.curso.domains.CursoResponseDom;
import com.cronograma.api.useCases.curso.implement.mappers.CursoMapper;
import com.cronograma.api.useCases.curso.implement.repositorys.*;
import com.cronograma.api.useCases.usuario.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CursoService {

    private final CursoRepository cursoRepository;
    private final CursoCoordenadorRepository cursoCoordenadorRepository;
    private final CursoFaseRepository cursoFaseRepository;
    private final CursoCronogramaRepository cursoCronogramaRepository;

    private final CursoMapper cursoMapper;

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
        aa();
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

    public void aa(){
//        Usuario usuario = usuarioService.buscarUsuarioAutenticado();

        List<Cronograma> cronogramas = cursoCronogramaRepository.findAll();


        cronogramas.stream().forEach(cronograma -> {
            cronograma.getCurso().getNome();
            List<Fase> fases = new ArrayList<>();
            for (DiaCronograma diaCronograma : cronograma.getDiasCronograma()){
               if(!fases.contains(diaCronograma.getFase())){
                   fases.add(diaCronograma.getFase());
               }
            }
            //trasnformar isso em um group by

            for (Fase fase:fases){
                System.out.println(fase.getNumero());
            }
        });


//        if(
//            usuario.getAluno() != null &&
//            usuario.getNiveisAcesso().stream().noneMatch(nivelAcesso -> nivelAcesso.getRankingAcesso() < 4)
//        ){
//
//
//
//        } else if(
//             usuario.getProfessor() != null &&
//             usuario.getNiveisAcesso().stream().noneMatch(nivelAcesso -> nivelAcesso.getRankingAcesso() < 3)
//        ){
//            List<Long> cursoIds = usuario.getProfessor().getDisciplinas().stream()
//        } else if(
//             usuario.getCoordenador() != null &&
//             usuario.getNiveisAcesso().stream().noneMatch(nivelAcesso -> nivelAcesso.getRankingAcesso() < 2)
//        ){
//            List<Long> cursoIds = usuario.getCoordenador().getCursos().stream().map(Curso::getId).toList();
//            List<Cronograma> cronogramaEncontrado = cursoCronogramaRepository.findAllByCursoIds(cursoIds);
//
//            cronogramaEncontrado.stream().map(jso -> jso.getDiasCronograma().stream())
//
//        } else {
//
//        }


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

        if(curso.getSigla()== null || curso.getSigla().isBlank()){
            errorMessages.add("Sigla é um campo obrigatório!");
        }

        if (curso.getFaseIds() == null || curso.getFaseIds().isEmpty()){
            errorMessages.add("Fase é um campo obrigatório!");
        }

        if(!errorMessages.isEmpty()){
            throw new CursoException(errorMessages);
        }
    }
}
