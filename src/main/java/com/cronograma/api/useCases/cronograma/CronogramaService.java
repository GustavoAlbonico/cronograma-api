package com.cronograma.api.useCases.cronograma;

import com.cronograma.api.entitys.*;
import com.cronograma.api.entitys.enums.BooleanEnum;
import com.cronograma.api.entitys.enums.DiaSemanaEnum;
import com.cronograma.api.entitys.enums.StatusEnum;
import com.cronograma.api.useCases.cronograma.domains.*;
import com.cronograma.api.useCases.dataBloqueada.implement.repositorys.DataBloqueadaRepository;
import com.cronograma.api.useCases.diaSemanaDisponivel.implement.repositorys.DiaSemanaDisponivelRepository;
import com.cronograma.api.useCases.disciplina.implement.repositorys.DisciplinaRepository;
import com.cronograma.api.useCases.fase.implement.repositorys.FaseRepository;
import com.cronograma.api.useCases.periodo.implement.repositorys.PeriodoRepository;
import com.cronograma.api.useCases.professor.implement.repositorys.ProfessorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CronogramaService {

    @Autowired
    private DisciplinaRepository disciplinaRepository;

    @Autowired
    private PeriodoRepository periodoRepository;

    @Autowired
    private FaseRepository faseRepository;

    @Autowired
    private ProfessorRepository professorRepository;

    @Autowired
    private DataBloqueadaRepository dataBloqueadaRepository;

    @Autowired
    private DiaSemanaDisponivelRepository diaSemanaDisponivelRepository;


    public List<TesteResponseCronogramaDom> gerarCronograma(CronogramaRequestDom cronograma){

        Periodo periodoAtivo = buscarPeriodoAtivoAtual();

        List<DataBloqueada> datasBloqueadas = dataBloqueadaRepository.findAll();

        Map<DiaSemanaEnum,Double> quantidadeAulasPorDiaDaSemana =
                buscarQuantidadeAulasDisponveisPorDiaDaSemana(periodoAtivo,datasBloqueadas);

        List<Fase> fases = faseRepository.buscarFasesPorCursoId(cronograma.cursoId()).get();
        Set<Disciplina> disciplinasEncontradas = disciplinaRepository.findByCursoId(cronograma.cursoId()).get();

        validarQuantidadeDiasAulaLetivosDisponiveisPorPeriodo(disciplinasEncontradas,fases,quantidadeAulasPorDiaDaSemana);
        validarProfessorPossuiDiaSemanaDisponivelCadastrado(disciplinasEncontradas);
        validarDisciplinaExtensaoPossuiProfessorDiaSemanaDisponivelSabadoCadastrado(disciplinasEncontradas);

        Map<Long,Map<Disciplina, Double>> disciplinasComDiasAulaNecessariosPorCurso =
                buscarDisciplinasComDiasAulaNecessariosPorCurso(disciplinasEncontradas,fases);

        List<CronogramaDisciplinaDom> cronogramaDisciplinasPorCurso =
                gerarCronogramaPorCurso(
                        disciplinasComDiasAulaNecessariosPorCurso,
                        fases,
                        quantidadeAulasPorDiaDaSemana);


        List<TesteResponseCronogramaDom>  response =
                cronogramaDisciplinasPorCurso
            .stream()
            .map(cronogramaDisciplinaDom -> {
            TesteResponseCronogramaDom testeResponseCronogramaDom =
                    new TesteResponseCronogramaDom(
                            cronogramaDisciplinaDom.getFaseId(),
                            cronogramaDisciplinaDom.getDisciplina().getNome(),
                            cronogramaDisciplinaDom.getDisciplina().getCargaHoraria(),
                            cronogramaDisciplinaDom.getOrdemPrioridadePorDiaSemana(),
                            cronogramaDisciplinaDom.getQuantidadeDiasAula(),
                            cronogramaDisciplinaDom.getDiaSemanaEnum(),
                            cronogramaDisciplinaDom.getDisciplina().getProfessor().getNomeCompleto()
                    );
            return testeResponseCronogramaDom;
        })
        .sorted(Comparator.comparing(TesteResponseCronogramaDom::faseId).thenComparing(TesteResponseCronogramaDom::diaSemanaEnum).thenComparing(TesteResponseCronogramaDom::quantidadeDiasAula))
        .toList();

        return response;
    }

    private void validarQuantidadeDiasAulaLetivosDisponiveisPorPeriodo(Set<Disciplina> disciplinasEncontradas,List<Fase> fases,Map<DiaSemanaEnum,Double> quantidadeAulasPorDiaDaSemana){

        final double quantidadeTotalDiaAulaDisponivelPorPeriodo = quantidadeAulasPorDiaDaSemana.values().stream().mapToDouble(Double::doubleValue).sum();

        for (Fase fase: fases){
            double quantidadeTotalDiaAulaNecessarioPorDisciplinas = disciplinasEncontradas.stream()
                    .filter(disciplina -> disciplina.getFase().equals(fase))
                    .mapToDouble(disciplina -> Math.floor(disciplina.getCargaHoraria() / disciplina.getCargaHorariaDiaria()))
                    .sum();

            if (quantidadeTotalDiaAulaNecessarioPorDisciplinas > quantidadeTotalDiaAulaDisponivelPorPeriodo){
                throw new RuntimeException("Não existe dias letivos suficiente para a " + fase.getNumero() + "º fase!");
            }
        }
    }

    private void validarProfessorPossuiDiaSemanaDisponivelCadastrado(Set<Disciplina> disciplinasEncontradas){
        for (Disciplina disciplina : disciplinasEncontradas){
            if (disciplina.getProfessor() != null){
                if (disciplina.getProfessor().getDiasSemanaDisponivel().isEmpty()){
                    throw new RuntimeException(
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
                   throw new RuntimeException(
                           "O professor " + disciplina.getProfessor().getNomeCompleto() +
                                   " da disciplina " + disciplina.getNome() +
                                   " que pertence a " + disciplina.getFase().getNumero() + "º fase não possui SABADO cadastrado como dia semana disponivel!");
               }
            }
        }
    }
    private List<CronogramaDisciplinaDom> gerarCronogramaPorCurso(Map<Long,Map<Disciplina, Double>> disciplinasComDiasAulaNecessariosPorCurso,
                                                                 List<Fase> fases,
                                                                 Map<DiaSemanaEnum,Double> quantidadeAulasPorDiaDaSemana)
    {

        Stack<CronogramaDisciplinaConflitanteDom> disciplinasComDiasSemanaConflitantes = new Stack<>();
        List<CronogramaDisciplinaConflitanteDom> disciplinasConflitantesVerificadas =  new ArrayList<>();

        int nivelConflito = 0;
        boolean existePossibilidades = true;
        while (existePossibilidades){
            boolean existeConflito = false;

            List<CronogramaDisciplinaDom> cronogramaDisciplinasPorCurso = new ArrayList<>();

            for (Fase fase : fases) {

                Map<Disciplina, Double> disciplinasComDiasAulaNecessariosPorFase = new LinkedHashMap<>(disciplinasComDiasAulaNecessariosPorCurso.get(fase.getId()));

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
                                        diaSemanaEnum);

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

          atualizarOrdemDePrioridadePorDiaSemana(cronogramaDisciplinasPorCurso);//talvez criar verificacao para desempenho

            return cronogramaDisciplinasPorCurso;
        }

        throw new RuntimeException("conflito");
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
                                               final DiaSemanaEnum diaSemanaEnum)
    {

        final double quantidadeAulasOcupadasNoDiaSemana = cronogramaDisciplinasPorCurso.stream()
                .filter(cronogramaDisciplinaDom ->
                        cronogramaDisciplinaDom.getDisciplina().getProfessor().getId().equals(disciplina.getProfessor().getId()) &&
                        cronogramaDisciplinaDom.getDiaSemanaEnum().equals(diaSemanaEnum))
                .map(CronogramaDisciplinaDom::getQuantidadeDiasAula)
                .mapToDouble(Double::doubleValue)
                .sum();

     return quantidadeAulasPorDiaSemanaOriginal.get(diaSemanaEnum) - quantidadeAulasOcupadasNoDiaSemana;
    }

    private void atualizarOrdemDePrioridadePorDiaSemana(List<CronogramaDisciplinaDom> cronogramaDisciplinasPorCurso){

        Map<Long, Map<DiaSemanaEnum, List<CronogramaDisciplinaDom>>> cronogramaDisciplinasPorFaseEPorDiaSemana = cronogramaDisciplinasPorCurso.stream()
                        .collect(Collectors.groupingBy(CronogramaDisciplinaDom::getFaseId, Collectors.groupingBy(CronogramaDisciplinaDom::getDiaSemanaEnum)));

        for (Map<DiaSemanaEnum, List<CronogramaDisciplinaDom>> cronogramaDisciplinasPorFaseSeparadoPorDiaSemanaEnum : cronogramaDisciplinasPorFaseEPorDiaSemana.values()) {
            for (DiaSemanaEnum diaSemanaEnum : cronogramaDisciplinasPorFaseSeparadoPorDiaSemanaEnum.keySet()) {
                for (int x = 1; x < cronogramaDisciplinasPorFaseSeparadoPorDiaSemanaEnum.get(diaSemanaEnum).size(); x++) {

                    final double quantidadeDiasAulaAnterior =
                            cronogramaDisciplinasPorFaseSeparadoPorDiaSemanaEnum.get(diaSemanaEnum).get(x - 1).getQuantidadeDiasAula();
                    final double quantidadeDiasAulaAtual =
                            cronogramaDisciplinasPorFaseSeparadoPorDiaSemanaEnum.get(diaSemanaEnum).get(x).getQuantidadeDiasAula();

                    if (quantidadeDiasAulaAnterior < 5) {//ajustar ordem de prioridade quando for 10/ 2/ 8 dias
                        if (quantidadeDiasAulaAtual > quantidadeDiasAulaAnterior) {
                            final int ordemPrioridadePorDiaSemanaAnterior =
                                    cronogramaDisciplinasPorFaseSeparadoPorDiaSemanaEnum.get(diaSemanaEnum).get(x - 1).getOrdemPrioridadePorDiaSemana();
                            final int ordemPrioridadePorDiaSemanaAtual =
                                    cronogramaDisciplinasPorFaseSeparadoPorDiaSemanaEnum.get(diaSemanaEnum).get(x).getOrdemPrioridadePorDiaSemana();

                            cronogramaDisciplinasPorFaseSeparadoPorDiaSemanaEnum.get(diaSemanaEnum).get(x).setOrdemPrioridadePorDiaSemana(ordemPrioridadePorDiaSemanaAnterior);
                            cronogramaDisciplinasPorFaseSeparadoPorDiaSemanaEnum.get(diaSemanaEnum).get(x - 1).setOrdemPrioridadePorDiaSemana(ordemPrioridadePorDiaSemanaAtual);
                        }
                    }
                }
            }
        }
    }

    private void adicionarDisciplinaCronograma(List<CronogramaDisciplinaDom> cronogramaDisciplinasPorCurso,CronogramaDisciplinaMelhorAproveitamentoDom cronogramaDisciplinaMelhorAproveitamento){

        int ordemPrioridadePorDiaSemana = 1;

        for (CronogramaDisciplinaDom cronogramaDisciplinaDom : cronogramaDisciplinasPorCurso){
            if(
              cronogramaDisciplinaDom.getDiaSemanaEnum().equals(cronogramaDisciplinaMelhorAproveitamento.getDiaSemanaEnum()) &&
              cronogramaDisciplinaDom.getFaseId().equals(cronogramaDisciplinaMelhorAproveitamento.getDisciplina().getFase().getId()) &&
              cronogramaDisciplinaDom.getOrdemPrioridadePorDiaSemana() == ordemPrioridadePorDiaSemana
            ){
              ordemPrioridadePorDiaSemana++;
            }
        }

        cronogramaDisciplinasPorCurso.add(new CronogramaDisciplinaDom(
                cronogramaDisciplinaMelhorAproveitamento.getDisciplina(),
                cronogramaDisciplinaMelhorAproveitamento.getDiaSemanaEnum(),
                cronogramaDisciplinaMelhorAproveitamento.getQuantidadeDiasAula(),
                ordemPrioridadePorDiaSemana,
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
                          .average().getAsDouble();

                  final double mediaQuantidadeDiaAulaDisponivel = cronogramaDisciplinasPorCurso.stream()
                          .filter(cronogramaDis -> cronogramaDis.getDisciplina().equals(cronogramaDisciplina.getDisciplina()))
                          .findFirst()
                          .map(cronogramaDisciplinaDom -> cronogramaDisciplinaDom.getDisciplina().getProfessor().getDiasSemanaDisponivel().stream()
                                  .mapToDouble(diaSemana -> quantidadeAulasPorDiaSemanaOriginal.get(diaSemana.getDiaSemanaEnum()))
                                  .average().getAsDouble()
                          ).get();

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

    private Periodo buscarPeriodoAtivoAtual() {
        Set<Periodo> periodoEncontrado =  periodoRepository.findByStatusEnum(StatusEnum.ATIVO).get();

        if(periodoEncontrado.size() > 1){
            throw new RuntimeException("Apenas um periodo pode estar ativo");
        } else if (periodoEncontrado.isEmpty()) {
            throw new RuntimeException("É necessario ter um periodo ativo");
        }

        return periodoEncontrado
                .stream()
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Período ativo não encontrado"));
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

            criarNovaInstanciaProfessor(disciplina);
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

    private void criarNovaInstanciaProfessor(Disciplina disciplina) {
        if(disciplina.getProfessor() != null){
            Set<DiaSemanaDisponivel> diasSemanaDisponiveis = new HashSet<>();

            for (DiaSemanaDisponivel diaSemanaDisponivel : disciplina.getProfessor().getDiasSemanaDisponivel()){
                DiaSemanaDisponivel diaSemanaDisponivelNovaInstancia = new DiaSemanaDisponivel();

                diaSemanaDisponivelNovaInstancia.setId(diaSemanaDisponivel.getId());
                diaSemanaDisponivelNovaInstancia.setDiaSemanaEnum(diaSemanaDisponivel.getDiaSemanaEnum());
                diaSemanaDisponivelNovaInstancia.setProfessor(diaSemanaDisponivel.getProfessor());
                diaSemanaDisponivelNovaInstancia.setStatusEnum(diaSemanaDisponivel.getStatusEnum());

                diasSemanaDisponiveis.add(diaSemanaDisponivelNovaInstancia);
            }

            Professor professorNovaInstancia =  new Professor();

            professorNovaInstancia.setId(disciplina.getProfessor().getId());
            professorNovaInstancia.setNomeCompleto(disciplina.getProfessor().getNomeCompleto());
            professorNovaInstancia.setCpf(disciplina.getProfessor().getCpf());
            professorNovaInstancia.setTelefone(disciplina.getProfessor().getTelefone());
            professorNovaInstancia.setStatusEnum(disciplina.getProfessor().getStatusEnum());
            professorNovaInstancia.setUsuario(disciplina.getProfessor().getUsuario());
            professorNovaInstancia.setDiasSemanaDisponivel(diasSemanaDisponiveis);
            professorNovaInstancia.setDisciplinas(disciplina.getProfessor().getDisciplinas());

            disciplina.setProfessor(professorNovaInstancia);
        }
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
