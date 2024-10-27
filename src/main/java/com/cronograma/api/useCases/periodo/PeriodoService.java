package com.cronograma.api.useCases.periodo;

import com.cronograma.api.entitys.Curso;
import com.cronograma.api.entitys.Disciplina;
import com.cronograma.api.entitys.Periodo;
import com.cronograma.api.entitys.Usuario;
import com.cronograma.api.entitys.enums.StatusEnum;
import com.cronograma.api.exceptions.CronogramaException;
import com.cronograma.api.exceptions.PeriodoException;
import com.cronograma.api.useCases.periodo.domains.PeriodoRequestDom;
import com.cronograma.api.useCases.periodo.domains.PeriodoResponseDom;
import com.cronograma.api.useCases.periodo.implement.mappers.PeriodoMapper;
import com.cronograma.api.useCases.periodo.implement.repositorys.*;
import com.cronograma.api.useCases.usuario.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PeriodoService {

    private final PeriodoRepository periodoRepository;
    private final PeriodoEventoRepository periodoEventoRepository;
    private final PeriodoAlunoRepository periodoAlunoRepository;
    private final PeriodoDataBloqueadaRepository periodoDataBloqueadaRepository;
    private final PeriodoProfessorRepository periodoProfessorRepository;
    private final PeriodoCursoRepository periodoCursoRepository;

    private final PeriodoMapper periodoMapper;

    private final UsuarioService usuarioService;

    @Transactional(readOnly = true)
    public List<PeriodoResponseDom> carregarPeriodoPorUsuario(){
       Usuario usuario = usuarioService.buscarUsuarioAutenticado();

       Set<Long> cursoIds =  new HashSet<>();

        if(usuario.getAluno() != null){
            cursoIds.add(usuario.getAluno().getCurso().getId());
        }

        if(usuario.getProfessor() != null){
            Set<Long> cursoIdsProfessor = usuario.getProfessor().getDisciplinas().stream()
                    .map(Disciplina::getCurso)
                    .map(Curso::getId).collect(Collectors.toSet());

            cursoIds.addAll(cursoIdsProfessor);
        }

        if(usuario.getCoordenador() != null){
            Set<Long> cursoIdsCoordenador = usuario.getCoordenador().getCursos().stream()
                    .map(Curso::getId).collect(Collectors.toSet());

            cursoIds.addAll(cursoIdsCoordenador);
        }

        if (usuario.getNiveisAcesso().stream().anyMatch(nivelAcesso -> nivelAcesso.getRankingAcesso() < 2)) {
            Set<Long> cursoIdsEncontrados = periodoCursoRepository.buscarTodosCursoIds();
            cursoIds.addAll(cursoIdsEncontrados);
        }

       List<Periodo> periodosEncontrados = periodoRepository.buscarTodosPorCursoIds(cursoIds);

       return periodosEncontrados.stream()
               .map(periodoMapper::periodoParaPeriodoResponseDom)
               .sorted(Comparator.comparing(PeriodoResponseDom::getDataInicial))
               .toList();
    }

    @Transactional(readOnly = true)
    public List<PeriodoResponseDom> carregarPeriodo(){
        List<Periodo> periodosEncontrados = periodoRepository.findAll();

        return periodosEncontrados.stream()
                .map(periodoMapper::periodoParaPeriodoResponseDom)
                .sorted(Comparator.comparing(PeriodoResponseDom::getStatusEnum)
                        .thenComparing(PeriodoResponseDom::getDataInicial))
                .toList();
    }

    @Transactional(readOnly = true)
    public PeriodoResponseDom carregarPeriodoPorId(Long id){
        Periodo periodoEncontrado = periodoRepository.findById(id)
                .orElseThrow(() -> new PeriodoException("Nenhum periodo encontrado!"));

        return periodoMapper.periodoParaPeriodoResponseDom(periodoEncontrado);
    }

    @Transactional
    public void criarPeriodo(PeriodoRequestDom periodoRequestDom){
        validarCampos(periodoRequestDom);
        inativarPeriodos();

        Periodo periodo = periodoMapper.periodoRequestDomParaPeriodo(periodoRequestDom);
        periodoRepository.save(periodo);

        periodoAlunoRepository.deleteAll();
        periodoProfessorRepository.excluirProfessorDiasSemanaDisponiveis();
        periodoDataBloqueadaRepository.deleteAll();
        periodoEventoRepository.deleteAll();
    }

    public void editarPeriodo(Long id, PeriodoRequestDom periodoRequestDom){
        Periodo periodoEncontrado = periodoRepository.findById(id)
                .orElseThrow(() -> new PeriodoException("Nenhum periodo encontrado!"));

        if (periodoEncontrado.getStatusEnum().equals(StatusEnum.INATIVO)){
            throw new PeriodoException("Não é possivel alterar um periodo inativo!");
        }
        if (!periodoEncontrado.getCronogramas().isEmpty()){
            throw new PeriodoException("Não é possivel alterar um periodo que ja possui cronogramas relacionado!");
        }

        validarCampos(periodoRequestDom);
        periodoMapper.periodoRequestDomParaPeriodoEncontrado(periodoRequestDom,periodoEncontrado);
        periodoRepository.save(periodoEncontrado);
    }

    public void excluirPeriodo(Long id){
        Periodo periodoEncontrado = periodoRepository.findById(id)
                .orElseThrow(() -> new PeriodoException("Nenhum periodo encontrado!"));

        if (!periodoEncontrado.getCronogramas().isEmpty()){
            throw new PeriodoException("O periodo está sendo utilizado em cronogramas!");
        }

        periodoRepository.deleteById(id);
    }

    private void inativarPeriodos(){
       Optional<Set<Periodo>> periodosEncontrados = periodoRepository.findAllByStatusEnum(StatusEnum.ATIVO);

       if (periodosEncontrados.isPresent()){
           Set<Periodo> periodosEntidade = periodosEncontrados.get().stream()
                   .peek(periodo -> periodo.setStatusEnum(StatusEnum.INATIVO))
                   .collect(Collectors.toSet());

           periodoRepository.saveAll(periodosEntidade);
       }
    }

    public Periodo buscarPeriodoAtivoAtual() {
        Set<Periodo> periodoEncontrado = periodoRepository.findAllByStatusEnum(StatusEnum.ATIVO)
                .filter(set -> !set.isEmpty())
                .orElseThrow(() -> new CronogramaException("É necessário ter um periodo ativo!"));

        if(periodoEncontrado.size() > 1){
            throw new CronogramaException("Apenas um periodo pode estar ativo!");
        } else if (periodoEncontrado.isEmpty()) {
            throw new CronogramaException("É necessário ter um periodo ativo!");
        } else {
            return periodoEncontrado.stream().findFirst()
                    .orElseThrow(() -> new CronogramaException("Nenhum periodo encontrado!"));
        }
    }

    private void validarCampos(PeriodoRequestDom periodo){
        List<String> errorMessages =  new ArrayList<>();

        if (periodo.getNome() == null || periodo.getNome().isBlank()){
            errorMessages.add("Nome é um campo obrigatório!");
        }

        if(periodo.getDataFinal() == null){
            errorMessages.add("Data Final é um campo obrigatório!");
        }

        if(periodo.getDataInicial() == null){
            errorMessages.add("Data Inicial é um campo obrigatório!");
        }

        if(periodo.getDataFinal() != null && periodo.getDataInicial() != null){
            if(periodo.getDataInicial().isAfter(periodo.getDataFinal())){
                errorMessages.add("Data inicial precisa ser menor que a Data Final!");
            } else if (periodo.getDataInicial().isEqual(periodo.getDataFinal())) {
                errorMessages.add("Data inicial precisa ser diferente da Data Final!");
            }
        }

        if(!errorMessages.isEmpty()){
            throw new PeriodoException(errorMessages);
        }
    }
}
