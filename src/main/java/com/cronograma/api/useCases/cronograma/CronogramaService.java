package com.cronograma.api.useCases.cronograma;

import com.cronograma.api.entitys.*;
import com.cronograma.api.entitys.enums.*;
import com.cronograma.api.exceptions.CronogramaException;
import com.cronograma.api.useCases.cronograma.domains.*;
import com.cronograma.api.useCases.cronograma.implement.mappers.CronogramaDiaCronogramaMapper;
import com.cronograma.api.useCases.cronograma.implement.mappers.CronogramaDisciplinaMapper;
import com.cronograma.api.useCases.cronograma.implement.mappers.CronogramaMapper;
import com.cronograma.api.useCases.cronograma.implement.repositorys.*;
import com.cronograma.api.useCases.diaCronograma.DiaCronogramaService;
import com.cronograma.api.useCases.periodo.PeriodoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
public class CronogramaService {

    @Autowired
    private CronogramaDisciplinaRepository cronogramaDisciplinaRepository;

    @Autowired
    private CronogramaFaseRepository cronogramaFaseRepository;

    @Autowired
    private CronogramaDataBloqueadaRepository cronogramaDataBloqueadaRepository;

    @Autowired
    private CronogramaCursoRepository cronogramaCursoRepository;

    @Autowired
    private CronogramaPeriodoRepository cronogramaPeriodoRepository;

    @Autowired
    private CronogramaRepository cronogramaRepository;

    @Autowired
    private CronogramaDiaCronogramaRepository cronogramaDiaCronogramaRepository;

    @Autowired
    private CronogramaMapper cronogramaMapper;

    @Autowired
    private CronogramaDisciplinaMapper cronogramaDisciplinaMapper;

    @Autowired
    private CronogramaDiaCronogramaMapper cronogramaDiaCronogramaMapper;

    private final PeriodoService periodoService;
    private final DiaCronogramaService diaCronogramaService;

    public CronogramaService(PeriodoService periodoService, DiaCronogramaService diaCronogramaService) {
        this.periodoService = periodoService;
        this.diaCronogramaService = diaCronogramaService;
    }

    public CronogramaResponseDom carregarCronograma(Long periodoId,Long cursoId,Long faseId){

        Periodo periodo = cronogramaPeriodoRepository.findById(periodoId).orElseThrow(() -> new CronogramaException("Nenhum periodo encontrado!"));
        Curso curso = cronogramaCursoRepository.findById(cursoId).orElseThrow(() -> new CronogramaException("Nenhum curso encontrado!"));
        Fase fase = cronogramaFaseRepository.findById(faseId).orElseThrow(() -> new CronogramaException("Nenhuma fase encontrada!"));

        List<Disciplina> disciplinas = cronogramaDisciplinaRepository.findAllByCursoIdAndFaseId(cursoId,faseId);

        List<CronogramaDisciplinaResponseDom> disciplinasReponse =
                cronogramaDisciplinaMapper.listaDisciplinaParaListaCronogramaDisciplinaResponseDom(disciplinas);

        List<DiaCronograma> diasCronogramaEncontrado = cronogramaDiaCronogramaRepository.buscarTodosPorCursoIdFaseId(cursoId,faseId);
        List<CronogramaDiaCronogramaResponseDom>  diasCronogramaResponse =
                cronogramaDiaCronogramaMapper.listaDiaCronogramaParaListaCronogramaDiaCronogramaResponseDom(diasCronogramaEncontrado);

        Map<MesEnum,Map<DiaSemanaEnum,List<CronogramaDiaCronogramaResponseDom>>> diasCronogramaResponseOrdenado = diasCronogramaResponse.stream()
                .collect(Collectors.groupingBy(
                        diaCronograma -> MesEnum.monthParaMesEnum(diaCronograma.getData().getMonth()),
                        LinkedHashMap::new,
                        Collectors.groupingBy(
                                CronogramaDiaCronogramaResponseDom::getDiaSemanaEnum,
                                LinkedHashMap::new,
                                Collectors.collectingAndThen(
                                        Collectors.toList(),
                                        listaDiaCronogramaResponse -> listaDiaCronogramaResponse.stream()
                                                .sorted(Comparator.comparing(CronogramaDiaCronogramaResponseDom::getData))
                                                .toList()
                                        )
                                )
                        )
                );

        CronogramaResponseDom cronogramaResponseDom =
                new CronogramaResponseDom(
                        curso.getNome(),
                        fase.getNumero(),
                        periodo.getDataInicial().getYear(),
                        disciplinasReponse,
                        diasCronogramaResponseOrdenado
                );

        return cronogramaResponseDom;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void deletarCronograma(Long id){
        cronogramaRepository.deleteById(id);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Long gerarCronogramaPorCursos(CronogramaRequestDom cronograma){

        validarExisteCronograma(cronograma);

        Periodo periodoAtivo = periodoService.buscarPeriodoAtivoAtual();

        List<DataBloqueada> datasBloqueadas = cronogramaDataBloqueadaRepository.findAll();

        Map<DiaSemanaEnum, Double> quantidadeAulasPorDiaDaSemana =
                buscarQuantidadeAulasDisponveisPorDiaDaSemana(periodoAtivo, datasBloqueadas);

        List<Fase> fases = cronogramaFaseRepository.buscarFasesPorCursoId(cronograma.getCursoId())
                .orElseThrow(() -> new CronogramaException("Nenhuma fase encontrada!"));

        Set<Disciplina> disciplinasEncontradas = cronogramaDisciplinaRepository.findByCursoId(cronograma.getCursoId())
                .orElseThrow(() -> new CronogramaException("Nenhuma disciplina encontrada!"));

        Set<Disciplina> disciplinas = cronogramaDisciplinaMapper.disciplinasParaDisciplinasNovaInstancia(disciplinasEncontradas);

        validarQuantidadeDiasAulaLetivosDisponiveisPorPeriodo(disciplinas, fases, quantidadeAulasPorDiaDaSemana);
        validarProfessorPossuiDiaSemanaDisponivelCadastrado(disciplinas);
        validarDisciplinaExtensaoPossuiProfessorDiaSemanaDisponivelSabadoCadastrado(disciplinas);

        Map<Long, Map<Disciplina, Double>> disciplinasComDiasAulaNecessariosPorCurso =
                buscarDisciplinasComDiasAulaNecessariosPorCurso(disciplinas, fases);


        Set<Long> professoresId = disciplinas.stream().map(disciplina -> disciplina.getProfessor().getId()).collect(Collectors.toSet());
        List<DiaCronograma> diasCronogramaReferenteProfessoresCursoAtual = cronogramaDiaCronogramaRepository.buscarTodosPorProfessoresId(professoresId);
        final Map<Long,Map<DiaSemanaEnum, Double>> professoresLecionandoEmOutroCursoComQuantidadeDiaSemanaOcupados = diasCronogramaReferenteProfessoresCursoAtual.stream()
                .collect(Collectors.groupingBy(diaCronograma -> diaCronograma.getDisciplina().getProfessor().getId(),
                        LinkedHashMap::new,
                        Collectors.groupingBy(DiaCronograma::getDiaSemanaEnum,
                            LinkedHashMap::new,
                            Collectors.collectingAndThen(
                                    Collectors.toList(),
                                    diasCronograma -> Double.valueOf(diasCronograma.size())
                            ))
                        )
                );

        List<CronogramaDisciplinaDom> cronogramaDisciplinasPorCurso =
                gerarCronogramaPorCurso(
                        disciplinasComDiasAulaNecessariosPorCurso,
                        fases,
                        quantidadeAulasPorDiaDaSemana,
                        professoresLecionandoEmOutroCursoComQuantidadeDiaSemanaOcupados);

        final Cronograma cronogramaEntidade = new Cronograma();
        cronogramaMapper.cronogramaRequestDomParaCronograma(cronograma,cronogramaEntidade,cronogramaCursoRepository,cronogramaPeriodoRepository);
        Cronograma cronogramaSalvo = cronogramaRepository.save(cronogramaEntidade);

        diaCronogramaService.criarDiaCronograma(
                cronogramaDisciplinasPorCurso,
                cronogramaSalvo,
                periodoAtivo,
                datasBloqueadas);

        return cronogramaSalvo.getId();
    }
    private void validarExisteCronograma(CronogramaRequestDom cronograma){
        Optional<Cronograma> cronogramaEncontrado = cronogramaRepository.findByCursoIdAndPeriodoId(cronograma.getCursoId(),cronograma.getPeriodoId());
        if (cronogramaEncontrado.isPresent()){
            throw new CronogramaException("É necessário excluir o cronograma antigo para gerar um novo cronograma!");
        }
    }
    private void validarQuantidadeDiasAulaLetivosDisponiveisPorPeriodo(Set<Disciplina> disciplinasEncontradas,List<Fase> fases,Map<DiaSemanaEnum,Double> quantidadeAulasPorDiaDaSemana){

        final double quantidadeTotalDiaAulaDisponivelPorPeriodo = quantidadeAulasPorDiaDaSemana.values().stream().mapToDouble(Double::doubleValue).sum();

        for (Fase fase: fases){
            double quantidadeTotalDiaAulaNecessarioPorDisciplinas = disciplinasEncontradas.stream()
                    .filter(disciplina -> disciplina.getFase().equals(fase))
                    .mapToDouble(disciplina -> Math.floor(disciplina.getCargaHoraria() / disciplina.getCargaHorariaDiaria()))
                    .sum();

            if (quantidadeTotalDiaAulaNecessarioPorDisciplinas > quantidadeTotalDiaAulaDisponivelPorPeriodo){
                throw new CronogramaException("Não existe dias letivos suficiente para a " + fase.getNumero() + "º fase!");
            }
        }
    }

    private void validarProfessorPossuiDiaSemanaDisponivelCadastrado(Set<Disciplina> disciplinasEncontradas){
        for (Disciplina disciplina : disciplinasEncontradas){
            if (disciplina.getProfessor() != null){
                if (disciplina.getProfessor().getDiasSemanaDisponivel().isEmpty()){
                    throw new CronogramaException(
                            "O professor " + disciplina.getProfessor().getNomeCompleto() +
                            " da disciplina " + disciplina.getNome() +
                            " que pertence a " + disciplina.getFase().getNumero() + "º fase não possui dia semana disponivel cadastrado!");
                }
            }
        }
    }

    private void validarDisciplinaExtensaoPossuiProfessorDiaSemanaDisponivelSabadoCadastrado(Set<Disciplina> disciplinasEncontradas){
        for (Disciplina disciplina : disciplinasEncontradas){
            if (disciplina.getExtensaoBooleanEnum().equals(BooleanEnum.SIM) && disciplina.getProfessor() != null){
               final boolean disciplinaExtensaoPossuiProfessorDiaSemanaDisponivelSabadoCadastrado =
                        disciplina.getProfessor().getDiasSemanaDisponivel().stream()
                                .noneMatch(diaSemanaDisponivel -> diaSemanaDisponivel.getDiaSemanaEnum().equals(DiaSemanaEnum.SABADO));

               if (disciplinaExtensaoPossuiProfessorDiaSemanaDisponivelSabadoCadastrado){
                   throw new CronogramaException(
                           "O professor " + disciplina.getProfessor().getNomeCompleto() +
                                   " da disciplina " + disciplina.getNome() +
                                   " que pertence a " + disciplina.getFase().getNumero() + "º fase não possui SABADO cadastrado como dia semana disponivel!");
               }
            }
        }
    }

    private List<CronogramaDisciplinaDom> gerarCronogramaPorCurso(Map<Long,Map<Disciplina, Double>> disciplinasComDiasAulaNecessariosPorCurso,
                                                                 List<Fase> fases,
                                                                 Map<DiaSemanaEnum,Double> quantidadeAulasPorDiaDaSemana,
                                                                 final Map<Long,Map<DiaSemanaEnum, Double>> professoresLecionandoEmOutroCursoComQuantidadeDiaSemanaOcupados)
    {

        Stack<CronogramaDisciplinaConflitanteDom> disciplinasComDiasSemanaConflitantes = new Stack<>();
        List<CronogramaDisciplinaConflitanteDom> disciplinasConflitantesVerificadas =  new ArrayList<>();

        int nivelConflito = 0;
        boolean existePossibilidades = true;
        while (existePossibilidades){
            boolean existeConflito = false;

            List<CronogramaDisciplinaDom> cronogramaDisciplinasPorCurso = new ArrayList<>();

            for (Fase fase : fases) {

                Map<Disciplina, Double> disciplinasComDiasAulaNecessariosPorFase =
                        new LinkedHashMap<>(
                                reordenarDisciplinasPorQuantidadeDisciplinaProfessorLecionandoDiaSemana(
                                        disciplinasComDiasAulaNecessariosPorCurso.get(fase.getId()),
                                        cronogramaDisciplinasPorCurso,
                                        quantidadeAulasPorDiaDaSemana)
                        );

                final boolean naoExisteDisciplinaExtensao = disciplinasComDiasAulaNecessariosPorFase.keySet().stream()
                    .filter(disciplina -> disciplina.getFase().getId().equals(fase.getId()))
                    .noneMatch(disciplina -> disciplina.getExtensaoBooleanEnum().equals(BooleanEnum.SIM));

                Map<DiaSemanaEnum, Double> quantidadeAulasPorDiaDaSemanaAuxiliar = new HashMap<>(quantidadeAulasPorDiaDaSemana);

                final Map<DiaSemanaEnum, Double> quantidadeAulasPorDiaSemanaOriginal = Map.copyOf(quantidadeAulasPorDiaDaSemana);

                adicionarDiasDaSemanaConflitantesVerificados(disciplinasComDiasAulaNecessariosPorFase, disciplinasConflitantesVerificadas);
                removerDiasDaSemanaConflitantes(disciplinasComDiasAulaNecessariosPorFase, disciplinasComDiasSemanaConflitantes);

                for (Map.Entry<Disciplina, Double> entry : disciplinasComDiasAulaNecessariosPorFase.entrySet()) {
                    while (entry.getValue() > 0) {
                        CronogramaDisciplinaMelhorAproveitamentoDom cronogramaDisciplinaMelhorAproveitamento = null;
                        boolean disciplinaMelhorAproveitamentoPrecisaVariosDiasDiasSemana = false;
                        for (DiaSemanaDisponivel diaSemanaDisponivel : entry.getKey().getProfessor().getDiasSemanaDisponivel()) {
                            final DiaSemanaEnum diaSemanaEnum = diaSemanaDisponivel.getDiaSemanaEnum();

                            if(!verificarDiaSemanaEnumLiberado(naoExisteDisciplinaExtensao,diaSemanaEnum,entry.getKey(),cronogramaDisciplinasPorCurso)){
                                continue;
                            }

                            final double quantidadeDiasAulaPorDiaSemana = quantidadeAulasPorDiaDaSemanaAuxiliar.get(diaSemanaEnum);

                            final boolean disciplinaCargaHorariaPequenaPrecisaVariosDiasSemanaAuxiliar =
                                    verificarDisciplinaCargaHorariaPequenaPrecisaVariosDias(
                                            cronogramaDisciplinasPorCurso,
                                            entry.getValue(),
                                            quantidadeDiasAulaPorDiaSemana,
                                            quantidadeAulasPorDiaSemanaOriginal.get(diaSemanaEnum),
                                            diaSemanaEnum,
                                            fase.getId(),
                                            entry.getKey());

                            final boolean disciplinaCargaHorariaGrandePrecisaVariosDiasSemana =
                                    entry.getValue() > quantidadeAulasPorDiaSemanaOriginal.get(diaSemanaEnum) &&
                                            (entry.getValue() - quantidadeDiasAulaPorDiaSemana) <= quantidadeDiasAulaPorDiaSemana;

                            double quantidadeDiasAulaNecessariosPorDisciplina;
                            boolean disciplinaCargaHorariaPequenaPrecisaVariosDiasSemana = false;
                            if (
                                disciplinaCargaHorariaPequenaPrecisaVariosDiasSemanaAuxiliar &&
                                quantidadeDiasAulaPorDiaSemana < entry.getValue() &&
                                !disciplinaCargaHorariaGrandePrecisaVariosDiasSemana
                            ){
                                disciplinaCargaHorariaPequenaPrecisaVariosDiasSemana = true;
                                quantidadeDiasAulaNecessariosPorDisciplina = quantidadeDiasAulaPorDiaSemana;
                            } else {
                                quantidadeDiasAulaNecessariosPorDisciplina = entry.getValue();
                            }

                            final double quantidadeDiasAulaRestantesNecessariosPorDisciplina =
                                    quantidadeDiasAulaNecessariosPorDisciplina - quantidadeDiasAulaPorDiaSemana;

                            double disciplinaPorcentagemOcupacaoDiasAulaPorDiaSemana =
                                (quantidadeDiasAulaNecessariosPorDisciplina / quantidadeDiasAulaPorDiaSemana) * 100;

                            double quantidadeDiasAulaRestantesPorDiaSemana =
                                quantidadeDiasAulaPorDiaSemana - quantidadeDiasAulaNecessariosPorDisciplina;


                            final double quantidadeAulasRestantesNoDiaSemanaPorProfessor  =
                                    buscarQuantidadeAulasRestantesNoDiaSemanaPorProfessor(
                                        cronogramaDisciplinasPorCurso,
                                        quantidadeAulasPorDiaSemanaOriginal,
                                        entry.getKey(),
                                        diaSemanaEnum,
                                        professoresLecionandoEmOutroCursoComQuantidadeDiaSemanaOcupados);

                            final boolean existeConflitoFases =
                                    (quantidadeDiasAulaRestantesNecessariosPorDisciplina < 0 ? entry.getValue() : entry.getValue() - quantidadeDiasAulaRestantesNecessariosPorDisciplina)
                                    > quantidadeAulasRestantesNoDiaSemanaPorProfessor;

                            final boolean disciplinaCargaHorariaPequenaComConflitoFasesaPrecisaVariosDias =
                                    verificarDisciplinaCargaHorariaPequenaComConflitoFasesPrecisaVariosDias(
                                            entry.getValue(),
                                            quantidadeAulasPorDiaSemanaOriginal.get(diaSemanaEnum),
                                            existeConflitoFases);

                            if (disciplinaCargaHorariaPequenaComConflitoFasesaPrecisaVariosDias){
                                disciplinaCargaHorariaPequenaPrecisaVariosDiasSemana = true;

                                quantidadeDiasAulaNecessariosPorDisciplina = quantidadeAulasRestantesNoDiaSemanaPorProfessor;

                                disciplinaPorcentagemOcupacaoDiasAulaPorDiaSemana =
                                        (quantidadeDiasAulaNecessariosPorDisciplina / quantidadeDiasAulaPorDiaSemana) * 100;

                                quantidadeDiasAulaRestantesPorDiaSemana =
                                        quantidadeDiasAulaPorDiaSemana - quantidadeDiasAulaNecessariosPorDisciplina;
                            }

                            if (
                                (!existeConflitoFases || disciplinaCargaHorariaPequenaComConflitoFasesaPrecisaVariosDias) &&
                                quantidadeDiasAulaNecessariosPorDisciplina > 0 &&
                                (quantidadeDiasAulaNecessariosPorDisciplina <= quantidadeDiasAulaPorDiaSemana || disciplinaCargaHorariaGrandePrecisaVariosDiasSemana)
                            ) {

                                final boolean existeMelhorAproveitamentoDias =
                                        validarExisteMelhorAproveitamentoDias(
                                                disciplinaPorcentagemOcupacaoDiasAulaPorDiaSemana,
                                                quantidadeDiasAulaRestantesPorDiaSemana,
                                                cronogramaDisciplinaMelhorAproveitamento,
                                                cronogramaDisciplinasPorCurso,
                                                diaSemanaEnum,
                                                fase);

                                if (existeMelhorAproveitamentoDias) {
                                    final double quantidadeDiasAulas;
                                    final double quantidadeDiasAulaRestantesNecessariosPorDisciplinaAtualizado;

                                  if (disciplinaCargaHorariaPequenaPrecisaVariosDiasSemana){
                                      quantidadeDiasAulas = quantidadeDiasAulaNecessariosPorDisciplina;
                                      quantidadeDiasAulaRestantesNecessariosPorDisciplinaAtualizado =  Math.abs(quantidadeDiasAulaNecessariosPorDisciplina - entry.getValue());
                                  }  else {
                                      quantidadeDiasAulas = quantidadeDiasAulaRestantesNecessariosPorDisciplina < 0 ? entry.getValue() : entry.getValue() - quantidadeDiasAulaRestantesNecessariosPorDisciplina;
                                      quantidadeDiasAulaRestantesNecessariosPorDisciplinaAtualizado = quantidadeDiasAulaRestantesNecessariosPorDisciplina;
                                  }

                                    cronogramaDisciplinaMelhorAproveitamento =
                                            new CronogramaDisciplinaMelhorAproveitamentoDom(
                                                    entry.getKey(),
                                                    diaSemanaEnum,
                                                    quantidadeDiasAulaRestantesPorDiaSemana,
                                                    quantidadeDiasAulaRestantesNecessariosPorDisciplinaAtualizado,
                                                    disciplinaPorcentagemOcupacaoDiasAulaPorDiaSemana,
                                                    quantidadeDiasAulas);

                                    disciplinaMelhorAproveitamentoPrecisaVariosDiasDiasSemana =
                                            disciplinaCargaHorariaGrandePrecisaVariosDiasSemana || disciplinaCargaHorariaPequenaPrecisaVariosDiasSemana;
                                }
                            }
                        }

                        if (cronogramaDisciplinaMelhorAproveitamento != null) {

                            adicionarDisciplinaCronograma(cronogramaDisciplinasPorCurso, cronogramaDisciplinaMelhorAproveitamento);

                            atualizarQuantidadeDiasAula(quantidadeAulasPorDiaDaSemanaAuxiliar,
                                    disciplinasComDiasAulaNecessariosPorFase,
                                    cronogramaDisciplinaMelhorAproveitamento);
                        }

                         existeConflito =
                                disciplinasComDiasAulaNecessariosPorFase.get(entry.getKey()) > 0 &&
                                !disciplinaMelhorAproveitamentoPrecisaVariosDiasDiasSemana;

                        if (existeConflito) {

                            final boolean nivelVerificado = buscarDisciplinaComDiaSemanaConflitante(
                                    quantidadeAulasPorDiaSemanaOriginal,
                                    cronogramaDisciplinasPorCurso,
                                    entry.getKey(),
                                    disciplinasComDiasSemanaConflitantes,
                                    nivelConflito,
                                    disciplinasConflitantesVerificadas);

                            nivelConflito = nivelVerificado ? nivelConflito - 1 : nivelConflito + 1;

                            if (nivelConflito < 0){
                                existePossibilidades =  false;
                            }
                            break;
                        }
                    }
                    if (existeConflito){
                        break;
                    }
                }
                if (existeConflito){
                    break;
                }
            }
            if(existeConflito){
                continue;
            }
            return adicionarOrdemDePrioridadePorDiaSemana(cronogramaDisciplinasPorCurso);
        }

        throw new CronogramaException("Conflito encontrado");
    }

    private Map<Disciplina, Double> reordenarDisciplinasPorQuantidadeDisciplinaProfessorLecionandoDiaSemana(Map<Disciplina, Double> disciplinasComDiasAulaNecessariosPorFase,
                                                                                                            List<CronogramaDisciplinaDom> cronogramaDisciplinasPorCurso,
                                                                                                            Map<DiaSemanaEnum, Double> quantidadeAulasPorDiaDaSemana
    ){
            if (cronogramaDisciplinasPorCurso.isEmpty()){
                return disciplinasComDiasAulaNecessariosPorFase;
            }

            return disciplinasComDiasAulaNecessariosPorFase.entrySet().stream()
                        .collect(Collectors.toMap(
                                disciplinaDoubleEntry -> disciplinaDoubleEntry,
                                disciplinaDoubleEntry -> {
                                    final double mediaQuantidadeAulaDisponivelDiaSemana = disciplinaDoubleEntry.getKey().getProfessor().getDiasSemanaDisponivel().stream()
                                            .filter(diaSemanaDisponivel -> quantidadeAulasPorDiaDaSemana.containsKey(diaSemanaDisponivel.getDiaSemanaEnum()))
                                            .mapToDouble(diaSemanaDisponivel -> quantidadeAulasPorDiaDaSemana.get(diaSemanaDisponivel.getDiaSemanaEnum()))
                                            .average().getAsDouble();

                                    final double disciplinaPorcentagemOcupacaoDiasAulaPorDiaSemana =
                                            (disciplinasComDiasAulaNecessariosPorFase.get(disciplinaDoubleEntry.getKey()) / mediaQuantidadeAulaDisponivelDiaSemana) * 100;

                                    if (disciplinaPorcentagemOcupacaoDiasAulaPorDiaSemana < 75) {

                                        return cronogramaDisciplinasPorCurso.stream()
                                                .filter(cronogramaDisciplina -> cronogramaDisciplina.getDisciplina().getProfessor().getId()
                                                        .equals(disciplinaDoubleEntry.getKey().getProfessor().getId()))
                                                .collect(Collectors.groupingBy(CronogramaDisciplinaDom::getDisciplina))
                                                .entrySet()
                                                .stream()
                                                .anyMatch(cronogramas ->
                                                        cronogramas.getValue().size() > 1 &&
                                                                cronogramas.getValue().stream().anyMatch(cronograma -> cronograma.getQuantidadeDiasAula() <= 5));
                                    }
                                    return false;
                                }
                        ))
                       .entrySet()
                       .stream()
                       .sorted(Comparator
                               .comparing((Map.Entry<Map.Entry<Disciplina, Double>, Boolean> entry) -> entry.getKey().getKey().getExtensaoBooleanEnum().equals(BooleanEnum.NAO))
                               .thenComparing(Map.Entry.<Map.Entry<Disciplina, Double>, Boolean>comparingByValue().reversed())
                               .thenComparing(entry -> entry.getKey().getKey().getProfessor().getDiasSemanaDisponivel().size())
                               .thenComparing(Comparator.comparing((Map.Entry<Map.Entry<Disciplina, Double>, Boolean> entry) -> entry.getKey().getKey().getCargaHoraria()).reversed())
                       )
                       .collect(Collectors.toMap(
                               entry -> entry.getKey().getKey(),
                               entry -> entry.getKey().getValue(),
                               (e1,e2) -> e1,
                               LinkedHashMap::new
                       ));

    }

    private List<CronogramaDisciplinaDom> adicionarOrdemDePrioridadePorDiaSemana(List<CronogramaDisciplinaDom> cronogramaDisciplinasPorCurso){
            return cronogramaDisciplinasPorCurso.stream()
                .collect(Collectors.groupingBy(
                        cronograma -> Map.entry(cronograma.getDisciplina().getProfessor().getId(), cronograma.getDiaSemanaEnum()),
                        LinkedHashMap::new,
                        Collectors.toList()
                ))
                .values()
                .stream()
                .flatMap(cronogramas -> {
                    AtomicInteger ordemPrioridadePorDiaSemana = new AtomicInteger(1);

                    if (cronogramas.size() > 1 && cronogramas.stream().noneMatch(cronograma -> cronograma.getQuantidadeDiasAula() <= 5)){
                        return cronogramas.stream()
                                .sorted(Comparator.comparing(CronogramaDisciplinaDom::getQuantidadeDiasAula).reversed())
                                .peek(cronograma -> cronograma.setOrdemPrioridadePorDiaSemana(ordemPrioridadePorDiaSemana.getAndIncrement()));
                    } else {
                        return cronogramas.stream();
                    }
                })
                .collect(Collectors.groupingBy(
                        cronograma -> Map.entry(cronograma.getFaseId(), cronograma.getDiaSemanaEnum()),
                        LinkedHashMap::new,
                        Collectors.toList()
                ))
                .values()
                .stream()
                .flatMap(disciplinas -> {

                    AtomicInteger ordemPrioridadePorDiaSemana = new AtomicInteger(1);

                    List<Integer> ordensPrioridadesUtilizadas =
                            new ArrayList<>(disciplinas.stream().map(CronogramaDisciplinaDom::getOrdemPrioridadePorDiaSemana).toList());

                    return disciplinas.stream()
                            .sorted(Comparator.comparing(CronogramaDisciplinaDom::getQuantidadeDiasAula).reversed())
                            .peek(cronograma -> {

                                while(ordensPrioridadesUtilizadas.contains(ordemPrioridadePorDiaSemana.get())){
                                    ordemPrioridadePorDiaSemana.incrementAndGet();
                                }

                                if(cronograma.getOrdemPrioridadePorDiaSemana() == null){
                                    cronograma.setOrdemPrioridadePorDiaSemana(ordemPrioridadePorDiaSemana.get());
                                    ordensPrioridadesUtilizadas.add(ordemPrioridadePorDiaSemana.get());
                                }
                            });

                })
                .sorted(
                        Comparator.comparing(CronogramaDisciplinaDom::getFaseId)
                                .thenComparing(CronogramaDisciplinaDom::getDiaSemanaEnum)
                                .thenComparing(CronogramaDisciplinaDom::getOrdemPrioridadePorDiaSemana)
                )
                .toList();
    }
    private boolean verificarDisciplinaCargaHorariaPequenaPrecisaVariosDias(List<CronogramaDisciplinaDom> cronogramaDisciplinasPorCurso,
                                                         double quantidadeDiasAulaNecessariosPorDisciplina,
                                                         final double quantidadeDiasAulaPorDiaSemana,
                                                         double quantidadeAulasPorDiaSemanaOriginal,
                                                         final DiaSemanaEnum diaSemanaEnum,
                                                         Long faseId,
                                                         Disciplina disciplina
    ){
        final double disciplinaPorcentagemOcupacaoDiasAulaPorDiaSemanaOriginal =
                (quantidadeDiasAulaNecessariosPorDisciplina / quantidadeAulasPorDiaSemanaOriginal) * 100;

        final double disciplinaPorcentagemOcupacaoDiasAulaPorDiaSemana =
                (quantidadeDiasAulaNecessariosPorDisciplina / quantidadeDiasAulaPorDiaSemana) * 100;

        final boolean existeDisciplinaDiaSemana = cronogramaDisciplinasPorCurso.stream()
                .anyMatch(cronogramaDisciplina ->
                        cronogramaDisciplina.getFaseId().equals(faseId) &&
                                cronogramaDisciplina.getDiaSemanaEnum().equals(diaSemanaEnum));

        final boolean mesmaDisciplinaEncontrada = cronogramaDisciplinasPorCurso.stream()
                .anyMatch(cronogramaDisciplina ->
                        cronogramaDisciplina.getFaseId().equals(faseId) &&
                                cronogramaDisciplina.getDisciplina().equals(disciplina));

        return  existeDisciplinaDiaSemana &&
                disciplinaPorcentagemOcupacaoDiasAulaPorDiaSemanaOriginal < 75 &&
                disciplinaPorcentagemOcupacaoDiasAulaPorDiaSemana > 100 &&
                disciplinaPorcentagemOcupacaoDiasAulaPorDiaSemana <= 140 ||
                mesmaDisciplinaEncontrada;
    }

    private boolean verificarDisciplinaCargaHorariaPequenaComConflitoFasesPrecisaVariosDias(double quantidadeDiasAulaNecessariosPorDisciplina,
                                                                                            double quantidadeAulasPorDiaSemanaOriginal,
                                                                                            final boolean existeConflitoFases
    ){
        final double disciplinaPorcentagemOcupacaoDiasAulaPorDiaSemanaOriginal =
                (quantidadeDiasAulaNecessariosPorDisciplina / quantidadeAulasPorDiaSemanaOriginal) * 100;

        return  existeConflitoFases &&
                disciplinaPorcentagemOcupacaoDiasAulaPorDiaSemanaOriginal < 75;
    }


    private double buscarQuantidadeAulasRestantesNoDiaSemanaPorProfessor(List<CronogramaDisciplinaDom> cronogramaDisciplinasPorCurso,
                                                                         final Map<DiaSemanaEnum,Double> quantidadeAulasPorDiaSemanaOriginal,
                                                                         Disciplina disciplina,
                                                                         final DiaSemanaEnum diaSemanaEnum,
                                                                         final Map<Long,Map<DiaSemanaEnum, Double>> professoresLecionandoEmOutroCursoComQuantidadeDiaSemanaOcupados)
    {
        double quantidadeAulasOcupadasNoDiaSemanaOutroCurso = 0.0;
        if(
           professoresLecionandoEmOutroCursoComQuantidadeDiaSemanaOcupados.containsKey(disciplina.getProfessor().getId()) &&
           professoresLecionandoEmOutroCursoComQuantidadeDiaSemanaOcupados.get(disciplina.getProfessor().getId()).containsKey(diaSemanaEnum)
        ){
            quantidadeAulasOcupadasNoDiaSemanaOutroCurso = professoresLecionandoEmOutroCursoComQuantidadeDiaSemanaOcupados.get(disciplina.getProfessor().getId()).get(diaSemanaEnum);
        }

        final double quantidadeAulasOcupadasNoDiaSemanaCursoAtual = cronogramaDisciplinasPorCurso.stream()
                .filter(cronogramaDisciplinaDom ->
                        cronogramaDisciplinaDom.getDisciplina().getProfessor().getId().equals(disciplina.getProfessor().getId()) &&
                        cronogramaDisciplinaDom.getDiaSemanaEnum().equals(diaSemanaEnum))
                .map(CronogramaDisciplinaDom::getQuantidadeDiasAula)
                .mapToDouble(Double::doubleValue)
                .sum();

        final double quantidadeAulasOcupadasNoDiaSemana = quantidadeAulasOcupadasNoDiaSemanaOutroCurso + quantidadeAulasOcupadasNoDiaSemanaCursoAtual;

     return quantidadeAulasPorDiaSemanaOriginal.get(diaSemanaEnum) - quantidadeAulasOcupadasNoDiaSemana;
    }

    private void adicionarDisciplinaCronograma(List<CronogramaDisciplinaDom> cronogramaDisciplinasPorCurso,CronogramaDisciplinaMelhorAproveitamentoDom cronogramaDisciplinaMelhorAproveitamento){

        cronogramaDisciplinasPorCurso.add(new CronogramaDisciplinaDom(
                cronogramaDisciplinaMelhorAproveitamento.getDisciplina(),
                cronogramaDisciplinaMelhorAproveitamento.getDiaSemanaEnum(),
                cronogramaDisciplinaMelhorAproveitamento.getQuantidadeDiasAula(),
                null,
                cronogramaDisciplinaMelhorAproveitamento.getDisciplina().getFase().getId()));
    }

    private void atualizarQuantidadeDiasAula (Map<DiaSemanaEnum,Double> quantidadeAulasPorDiaDaSemanaAuxiliar,
                                              Map<Disciplina, Double> disciplinasComDiasAulaNecessariosPorFase,
                                              CronogramaDisciplinaMelhorAproveitamentoDom cronogramaDisciplinaMelhorAproveitamento)
    {
        final double quantidadeDiasAulaRestantesPorDiaSemana =
                (cronogramaDisciplinaMelhorAproveitamento.getQuantidadeDiasAulaRestantesPorDiaSemana() < 0) ?
                0.0 :
                cronogramaDisciplinaMelhorAproveitamento.getQuantidadeDiasAulaRestantesPorDiaSemana();

        final double quantidadeDiasAulaRestantesNecessariosPorDisciplina =
                (cronogramaDisciplinaMelhorAproveitamento.getQuantidadeDiasAulaRestantesNecessariosPorDisciplina() < 0) ?
                0.0 :
                cronogramaDisciplinaMelhorAproveitamento.getQuantidadeDiasAulaRestantesNecessariosPorDisciplina();

        quantidadeAulasPorDiaDaSemanaAuxiliar
                .put(cronogramaDisciplinaMelhorAproveitamento.getDiaSemanaEnum(), quantidadeDiasAulaRestantesPorDiaSemana);

        disciplinasComDiasAulaNecessariosPorFase
                .put(cronogramaDisciplinaMelhorAproveitamento.getDisciplina(),quantidadeDiasAulaRestantesNecessariosPorDisciplina);
    }

    private boolean buscarDisciplinaComDiaSemanaConflitante(final Map<DiaSemanaEnum, Double> quantidadeAulasPorDiaSemanaOriginal,
                                                            List<CronogramaDisciplinaDom> cronogramaDisciplinasPorCurso,
                                                            Disciplina disciplina,
                                                            Stack<CronogramaDisciplinaConflitanteDom> disciplinasComDiasSemanaConflitantes,
                                                            int nivelConflito,
                                                            List<CronogramaDisciplinaConflitanteDom> disciplinasConflitantesVerificadas)
    {

        for (CronogramaDisciplinaDom cronogramaDisciplina : cronogramaDisciplinasPorCurso) {
          for (DiaSemanaDisponivel diaSemanaDisponivel : disciplina.getProfessor().getDiasSemanaDisponivel()){
              if(
                 cronogramaDisciplina.getDiaSemanaEnum().equals(diaSemanaDisponivel.getDiaSemanaEnum()) &&
                 !cronogramaDisciplina.getDisciplina().equals(disciplina)
              ){

                  final double quantidadeDiasAulaNecessariosDisciplina = cronogramaDisciplinasPorCurso.stream()
                          .filter(cronogramaDisciplinaDom -> cronogramaDisciplinaDom.getDisciplina().equals(cronogramaDisciplina.getDisciplina()))
                          .map(cronogramaDisciplinaDom -> cronogramaDisciplinaDom.getDisciplina().getCargaHoraria() / cronogramaDisciplinaDom.getDisciplina().getCargaHorariaDiaria())
                          .mapToDouble(Double::doubleValue)
                          .average().orElse(0.0);

                  final double mediaQuantidadeDiaAulaDisponivel = cronogramaDisciplinasPorCurso.stream()
                          .filter(cronogramaDis -> cronogramaDis.getDisciplina().equals(cronogramaDisciplina.getDisciplina()))
                          .findFirst()
                          .map(cronogramaDisciplinaDom -> cronogramaDisciplinaDom.getDisciplina().getProfessor().getDiasSemanaDisponivel().stream()
                                  .mapToDouble(diaSemana -> quantidadeAulasPorDiaSemanaOriginal.get(diaSemana.getDiaSemanaEnum()))
                                  .average().orElse(0.0)
                          ).orElse(0.0);

                  final double quantidadeMinimaDiaDaSemanaNecessario =
                          Math.ceil(quantidadeDiasAulaNecessariosDisciplina / mediaQuantidadeDiaAulaDisponivel);

                  if(cronogramaDisciplina.getDisciplina().getProfessor().getDiasSemanaDisponivel().size() > quantidadeMinimaDiaDaSemanaNecessario) {

                      final boolean disciplinaConflitanteEstaVerificada = disciplinasConflitantesVerificadas.stream()
                              .anyMatch(disciplinaConflitanteVerificada ->
                                           disciplinaConflitanteVerificada.disciplina().equals(disciplina) &&
                                           disciplinaConflitanteVerificada.disciplinaConflitante().equals(cronogramaDisciplina.getDisciplina()) &&
                                           disciplinaConflitanteVerificada.diaSemanaDisponivelConflitante().getDiaSemanaEnum().equals(cronogramaDisciplina.getDiaSemanaEnum()) &&
                                           disciplinaConflitanteVerificada.nivelConflito() == nivelConflito);

                      if (disciplinaConflitanteEstaVerificada){
                          continue;
                      }

                      final DiaSemanaDisponivel diaSemanaDisponivelConflitante =  cronogramaDisciplina.getDisciplina().getProfessor().getDiasSemanaDisponivel().stream()
                              .filter(diaSemanaDisponivelProfessorConflitante ->
                                      diaSemanaDisponivelProfessorConflitante.getDiaSemanaEnum().equals(diaSemanaDisponivel.getDiaSemanaEnum()))
                              .findFirst().get();

                      CronogramaDisciplinaConflitanteDom cronogramaDisciplinaConflitanteDom = new CronogramaDisciplinaConflitanteDom(
                              disciplina,
                              cronogramaDisciplina.getDisciplina(),
                              diaSemanaDisponivelConflitante,
                              nivelConflito);

                      disciplinasComDiasSemanaConflitantes.push(cronogramaDisciplinaConflitanteDom);
                      disciplinasConflitantesVerificadas.add(cronogramaDisciplinaConflitanteDom);

                      return false;
                  }
              }
          }
        }

        if (!disciplinasComDiasSemanaConflitantes.isEmpty()){
            disciplinasComDiasSemanaConflitantes.pop();
        }

        return true;
    }

    private void removerDiasDaSemanaConflitantes(Map<Disciplina, Double> disciplinasComDiasAulaNecessariosPorFase,
                                                 Stack<CronogramaDisciplinaConflitanteDom> disciplinasComDiasSemanaConflitantes)
    {
        for (Map.Entry<Disciplina, Double> disciplinas : disciplinasComDiasAulaNecessariosPorFase.entrySet()){
            for (CronogramaDisciplinaConflitanteDom disciplinaConflitantes : disciplinasComDiasSemanaConflitantes){
                if (disciplinas.getKey().equals(disciplinaConflitantes.disciplinaConflitante())){
                    disciplinas.getKey().getProfessor().getDiasSemanaDisponivel()
                            .removeIf(diaSemanaDisponivel ->
                                    disciplinaConflitantes.diaSemanaDisponivelConflitante().getDiaSemanaEnum().equals(diaSemanaDisponivel.getDiaSemanaEnum()));
                }
            }
        }
    }

    private void adicionarDiasDaSemanaConflitantesVerificados(Map<Disciplina, Double> disciplinasComDiasAulaNecessariosPorFase,  List<CronogramaDisciplinaConflitanteDom> disciplinasConflitantesVerificadas) {

        for (Map.Entry<Disciplina, Double> disciplinas : disciplinasComDiasAulaNecessariosPorFase.entrySet()){
            for (CronogramaDisciplinaConflitanteDom disciplinaConflitanteVerificada : disciplinasConflitantesVerificadas){
                if (disciplinas.getKey().equals(disciplinaConflitanteVerificada.disciplinaConflitante())){
                    boolean diaSemanaDisponivelExistente =  disciplinas.getKey().getProfessor().getDiasSemanaDisponivel().stream()
                            .anyMatch(diaSemanaDisponivel ->
                                    diaSemanaDisponivel.getDiaSemanaEnum().equals(
                                            disciplinaConflitanteVerificada.diaSemanaDisponivelConflitante().getDiaSemanaEnum()));

                    if(!diaSemanaDisponivelExistente){
                        disciplinas.getKey().getProfessor().getDiasSemanaDisponivel()
                                .add(disciplinaConflitanteVerificada.diaSemanaDisponivelConflitante());

                        reoordenarDiaDaSemanaProfessor(disciplinas.getKey());
                    }
                }
            }
        }
    }

    private boolean validarExisteMelhorAproveitamentoDias(final double disciplinaPorcentagemOcupacaoDiasAulaPorDiaSemana,
                                                          final double quantidadeDiasAulaRestantesPorDiaSemana,
                                                          CronogramaDisciplinaMelhorAproveitamentoDom cronogramaDisciplinaMelhorAproveitamento,
                                                          List<CronogramaDisciplinaDom> cronogramaDisciplinasPorCurso,
                                                          final DiaSemanaEnum diaSemanaEnumAtual,
                                                          Fase fase)
    {
        boolean existeMelhorAproveitamentoDias = true;

        if (cronogramaDisciplinaMelhorAproveitamento != null){
            existeMelhorAproveitamentoDias = false;

            final boolean existeDisciplinaNoDiaSemanaEnumAtual =
                    cronogramaDisciplinasPorCurso.stream().anyMatch(cronogramaDisciplina ->
                            cronogramaDisciplina.getFaseId().equals(fase.getId()) &&
                            cronogramaDisciplina.getDiaSemanaEnum().equals(diaSemanaEnumAtual));

            final boolean existeDisciplinaNoDiaSemanaEnumSalvo =
                    cronogramaDisciplinasPorCurso.stream().anyMatch(cronogramaDisciplina ->
                            cronogramaDisciplina.getFaseId().equals(cronogramaDisciplinaMelhorAproveitamento.getDisciplina().getFase().getId()) &&
                            cronogramaDisciplina.getDiaSemanaEnum().equals(cronogramaDisciplinaMelhorAproveitamento.getDiaSemanaEnum()));

            if(
               disciplinaPorcentagemOcupacaoDiasAulaPorDiaSemana > 75.00 &&
               quantidadeDiasAulaRestantesPorDiaSemana < cronogramaDisciplinaMelhorAproveitamento.getQuantidadeDiasAulaRestantesPorDiaSemana() &&
               (cronogramaDisciplinaMelhorAproveitamento.getDisciplinaPorcentagemOcupacaoDiasAulaPorDiaSemana() > 75.00 ||
                existeDisciplinaNoDiaSemanaEnumAtual)
            ){
                existeMelhorAproveitamentoDias = true;
            } else if(existeDisciplinaNoDiaSemanaEnumSalvo){
                return existeMelhorAproveitamentoDias;
            } else if (
                    disciplinaPorcentagemOcupacaoDiasAulaPorDiaSemana <= 75.00 &&
                    quantidadeDiasAulaRestantesPorDiaSemana > cronogramaDisciplinaMelhorAproveitamento.getQuantidadeDiasAulaRestantesPorDiaSemana()
            ){
                existeMelhorAproveitamentoDias = true;
            }
        }

        return existeMelhorAproveitamentoDias;
    }

    private Map<DiaSemanaEnum,Double> buscarQuantidadeAulasDisponveisPorDiaDaSemana(Periodo periodo, List<DataBloqueada> datasBloqueadas){
        Map<DiaSemanaEnum,Double> quantidadeAulasPorDiaDaSemana = new HashMap<>();

        LocalDate dataInicialAuxiliar = periodo.getDataInicial();
        while(dataInicialAuxiliar.isBefore(periodo.getDataFinal().plusDays(1L))) {
            LocalDate dataInicialAuxiliarLocal = dataInicialAuxiliar;

            if (datasBloqueadas.stream().noneMatch(dataBloqueada -> dataBloqueada.getData().isEqual(dataInicialAuxiliarLocal))){
                DayOfWeek dayOfWeek = dataInicialAuxiliar.getDayOfWeek();
                if(!dayOfWeek.equals(DayOfWeek.SUNDAY)){
                    DiaSemanaEnum diaDaSemana = DiaSemanaEnum.dayOfWeekParaDiaSemanaEnum(dayOfWeek);
                    quantidadeAulasPorDiaDaSemana.merge(diaDaSemana, 1.0, Double::sum);
                }
            }
            dataInicialAuxiliar = dataInicialAuxiliar.plusDays(1);
        }

        return quantidadeAulasPorDiaDaSemana;
    }

    private Map<Long,Map<Disciplina, Double>> buscarDisciplinasComDiasAulaNecessariosPorCurso(Set<Disciplina> disciplinasEncontradas,List<Fase> fases){

        Map<Disciplina, Double> disciplinasComDiasAulaNecessariosPorPeriodo = new HashMap<>();
        Map<Long,Map<Disciplina, Double>> disciplinasComDiasAulaNecessariosPorCurso = new LinkedHashMap<>();

        Long professorContratandoId = -1L;
        for (Disciplina disciplina : disciplinasEncontradas){

            professorContratandoId = verificarDisciplinaNaoPossuiProfessor(professorContratandoId,disciplina);
            reoordenarDiaDaSemanaProfessor(disciplina);

           final double quantidadeDiasAulaNecessariosPorDisciplina =
                    Math.floor(disciplina.getCargaHoraria() / disciplina.getCargaHorariaDiaria());

            disciplinasComDiasAulaNecessariosPorPeriodo
                    .put(disciplina,quantidadeDiasAulaNecessariosPorDisciplina);
        }

        for (Fase fase :fases){

            Map<Disciplina, Double> disciplinasComDiasAulaNecessariosPorFase = disciplinasComDiasAulaNecessariosPorPeriodo.entrySet().stream()
                    .filter(entry -> entry.getKey().getFase().getId().equals(fase.getId()))
                    .sorted(Comparator.comparing((Map.Entry<Disciplina, Double> entry) -> entry.getKey().getExtensaoBooleanEnum().equals(BooleanEnum.NAO))
                            .thenComparing(entry -> entry.getKey().getProfessor().getDiasSemanaDisponivel().size())
                            .thenComparing(Comparator.comparing((Map.Entry<Disciplina, Double> entry) -> entry.getKey().getCargaHoraria()).reversed()))
                    .collect(Collectors.toMap(
                            Map.Entry::getKey,
                            Map.Entry::getValue,
                            (e1, e2) -> e1,
                            LinkedHashMap::new
                    ));

            disciplinasComDiasAulaNecessariosPorCurso.put(fase.getId(),disciplinasComDiasAulaNecessariosPorFase);
        }

        return disciplinasComDiasAulaNecessariosPorCurso;
    }

    private void reoordenarDiaDaSemanaProfessor(Disciplina disciplina){
        Set<DiaSemanaDisponivel> diaSemanaDisponivelOrdenado;
        if (disciplina.getExtensaoBooleanEnum().equals(BooleanEnum.SIM)){
            diaSemanaDisponivelOrdenado = disciplina.getProfessor().getDiasSemanaDisponivel().stream()
                    .sorted(Comparator.comparing(diaSemanaDisponivel -> diaSemanaDisponivel.getDiaSemanaEnum().ordinal()))
                    .sorted(Comparator.comparing(diaSemanaDisponivel -> !diaSemanaDisponivel.getDiaSemanaEnum().equals(DiaSemanaEnum.SABADO)))
                    .collect(Collectors.toCollection(LinkedHashSet::new));

        } else {
            diaSemanaDisponivelOrdenado = disciplina.getProfessor().getDiasSemanaDisponivel().stream()
                    .sorted(Comparator.comparing(diaSemanaDisponivel -> diaSemanaDisponivel.getDiaSemanaEnum().ordinal()))
                    .sorted(Comparator.comparing(diaSemanaDisponivel -> diaSemanaDisponivel.getDiaSemanaEnum().equals(DiaSemanaEnum.SABADO)))
                    .collect(Collectors.toCollection(LinkedHashSet::new));

        }
        disciplina.getProfessor().getDiasSemanaDisponivel().clear();
        disciplina.getProfessor().setDiasSemanaDisponivel(diaSemanaDisponivelOrdenado);
    }

    private Long verificarDisciplinaNaoPossuiProfessor(Long professorContratandoId,Disciplina disciplina){

        if (disciplina.getProfessor() == null){
            Set<DiaSemanaDisponivel> diasSemanaDisponiveis = new HashSet<>();

            for (DiaSemanaEnum diaSemanaEnum : DiaSemanaEnum.values()){
                if (!diaSemanaEnum.equals(DiaSemanaEnum.DOMINGO)){
                    DiaSemanaDisponivel diaSemanaDisponivel = new DiaSemanaDisponivel();
                    diaSemanaDisponivel.setDiaSemanaEnum(diaSemanaEnum);
                    diaSemanaDisponivel.setStatusEnum(StatusEnum.ATIVO);

                    diasSemanaDisponiveis.add(diaSemanaDisponivel);
                }
            }
            Professor contratando = new Professor();

            contratando.setId(professorContratandoId);
            contratando.setNomeCompleto("Contratando");
            contratando.setStatusEnum(StatusEnum.ATIVO);
            contratando.setDiasSemanaDisponivel(diasSemanaDisponiveis);

            disciplina.setProfessor(contratando);

            professorContratandoId--;
        }

        return professorContratandoId;
    }

    private boolean verificarDiaSemanaEnumLiberado(final boolean naoExisteDisciplinaExtensao,final DiaSemanaEnum diaSemanaEnum, Disciplina disciplina, List<CronogramaDisciplinaDom> cronogramaDisciplinasPorCurso){

        if(naoExisteDisciplinaExtensao){
            return true;
        }

        if (disciplina.getExtensaoBooleanEnum().equals(BooleanEnum.SIM) && !diaSemanaEnum.equals(DiaSemanaEnum.SABADO)){
            final boolean diaSemanaEnumLiberadoDiferenteSabado = cronogramaDisciplinasPorCurso.stream()
                    .anyMatch(cronogramaDisciplina ->
                            cronogramaDisciplina.getFaseId().equals(disciplina.getFase().getId()) &&
                            cronogramaDisciplina.getDisciplina().equals(disciplina) &&
                            cronogramaDisciplina.getDiaSemanaEnum().equals(DiaSemanaEnum.SABADO));

            return diaSemanaEnumLiberadoDiferenteSabado;
        } else if (disciplina.getExtensaoBooleanEnum().equals(BooleanEnum.NAO) && diaSemanaEnum.equals(DiaSemanaEnum.SABADO)) {
            final boolean diaSemanaEnumLiberadoSerSabado = cronogramaDisciplinasPorCurso.stream()
                    .anyMatch(cronogramaDisciplina ->
                            cronogramaDisciplina.getFaseId().equals(disciplina.getFase().getId()) &&
                            cronogramaDisciplina.getDisciplina().equals(disciplina) &&
                            !cronogramaDisciplina.getDiaSemanaEnum().equals(DiaSemanaEnum.SABADO));

            return diaSemanaEnumLiberadoSerSabado;
        }

        return true;
    }
}
