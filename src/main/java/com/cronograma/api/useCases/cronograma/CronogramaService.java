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

        Periodo periodoAtivo = buscarPeriodoAtivoAtual(); // fora do loop

        //validarProfessorPossuiDiaSemanaDisponivel(cronograma.cursoId()); //CRIAR

        //validacao da quantidade de dias disponiveis por periodo X disciplinas + data exececao

//        validarDiasDaSemanaNecessarios(cronograma.cursoId()); // fora do loop //REVER

        //VALIDAR DISCIPLINA EXTENSAO TEM SABADO

        List<DataBloqueada> datasBloqueadas = dataBloqueadaRepository.findAll();
        Map<DiaSemanaEnum,Double> quantidadeAulasPorDiaDaSemana = //filtrar data exececao
                buscarQuantidadeAulasDisponveisPorDiaDaSemana(periodoAtivo,datasBloqueadas);

        List<Fase> fases = faseRepository.buscarFasesPorCursoId(cronograma.cursoId()).get();

        Map<Long,Map<Disciplina, Double>> disciplinasComDiasAulaNecessariosPorCurso =
                buscarDisciplinasComDiasAulaNecessariosPorCurso(fases,cronograma.cursoId());

        Stack<CronogramaDisciplinaConflitanteDom> disciplinasComDiasSemanaConflitantes = new Stack<>();
        List<CronogramaDisciplinaConflitanteDom> disciplinasConflitantesVerificadas =  new ArrayList<>();

            List<CronogramaDisciplinaDom> cronogramaFaseOficial =
                    gerarCronogramaPorFase(
                            disciplinasComDiasAulaNecessariosPorCurso,
                            fases,
                            quantidadeAulasPorDiaDaSemana,
                            disciplinasComDiasSemanaConflitantes,
                            0,
                            disciplinasConflitantesVerificadas);


    List<TesteResponseCronogramaDom>  response =
            cronogramaFaseOficial
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
                            cronogramaDisciplinaDom.getDisciplina().getProfessor().getNomeCompleto(),
                            cronogramaDisciplinaDom.getDisciplina().getProfessor().getDiasSemanaDisponivel().stream().map(DiaSemanaDisponivel::getDiaSemanaEnum).toList()
                    );
            return testeResponseCronogramaDom;
        })
        .sorted(Comparator.comparing(TesteResponseCronogramaDom::faseId).thenComparing(TesteResponseCronogramaDom::diaSemanaEnum))
        .toList();

        return response;
    }

    private List<CronogramaDisciplinaDom> gerarCronogramaPorFase(Map<Long,Map<Disciplina, Double>> disciplinasComDiasAulaNecessariosPorCurso,
                                                                 List<Fase> fases,
                                                                 Map<DiaSemanaEnum,Double> quantidadeAulasPorDiaDaSemana,
                                                                 Stack<CronogramaDisciplinaConflitanteDom> disciplinasComDiasSemanaConflitantes,
                                                                 int nivelConflito,
                                                                 List<CronogramaDisciplinaConflitanteDom> disciplinasConflitantesVerificadas)
    {

        while (nivelConflito != -20){

        boolean existeConflito = false;

        List<CronogramaDisciplinaDom> cronogramaDisciplinasPorFase = new ArrayList<>();

        nivelConflito++;

        System.out.println(nivelConflito);

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

                        final double quantidadeDiasAulaNecessariosPorDisciplina = entry.getValue();

                        final double quantidadeDiasAulaPorDiaSemana = quantidadeAulasPorDiaDaSemanaAuxiliar.get(diaSemanaEnum);

                        final double quantidadeDiasAulaRestantesNecessariosPorDisciplina = quantidadeDiasAulaNecessariosPorDisciplina - quantidadeDiasAulaPorDiaSemana;

                        final double disciplinaPorcentagemOcupacaoDiasAulaPorDiaSemana =
                                (quantidadeDiasAulaNecessariosPorDisciplina / quantidadeDiasAulaPorDiaSemana) * 100;

                        final double quantidadeDiasAulaRestantesPorDiaSemana =
                                quantidadeDiasAulaPorDiaSemana - quantidadeDiasAulaNecessariosPorDisciplina;

                        final boolean disciplinaPrecisaVariosDiasSemana =
                                quantidadeDiasAulaNecessariosPorDisciplina > quantidadeAulasPorDiaSemanaOriginal.get(diaSemanaEnum) &&
                                        quantidadeDiasAulaRestantesNecessariosPorDisciplina <= quantidadeDiasAulaPorDiaSemana;

                        if(!verificarDiaSemanaEnumLiberado(naoExisteDisciplinaExtensao,diaSemanaEnum,entry.getKey(),cronogramaDisciplinasPorFase)){
                            continue;
                        }

                        final boolean existeConflitoFases = validarExisteConflitoFases( //tem conflito criar uma realidade paralela ?
                                cronogramaDisciplinasPorFase,
                                quantidadeAulasPorDiaSemanaOriginal,
                                entry.getKey(),
                                diaSemanaEnum,
                                entry.getValue(),
                                disciplinaPrecisaVariosDiasSemana);

                        if (
                            !existeConflitoFases &&
                            quantidadeDiasAulaNecessariosPorDisciplina > 0 &&
                            (quantidadeDiasAulaNecessariosPorDisciplina <= quantidadeDiasAulaPorDiaSemana || disciplinaPrecisaVariosDiasSemana)
                        ) {

                            final boolean existeMelhorAproveitamentoDias =
                                    validarExisteMelhorAproveitamentoDias(
                                            disciplinaPorcentagemOcupacaoDiasAulaPorDiaSemana,
                                            quantidadeDiasAulaRestantesPorDiaSemana,
                                            cronogramaDisciplinaMelhorAproveitamento,
                                            cronogramaDisciplinasPorFase,
                                            diaSemanaEnum);

                            if (existeMelhorAproveitamentoDias) {
                                cronogramaDisciplinaMelhorAproveitamento =
                                        new CronogramaDisciplinaMelhorAproveitamentoDom(
                                                entry.getKey(),
                                                diaSemanaEnum,
                                                quantidadeDiasAulaRestantesPorDiaSemana,
                                                quantidadeDiasAulaRestantesNecessariosPorDisciplina,
                                                disciplinaPorcentagemOcupacaoDiasAulaPorDiaSemana,
                                                quantidadeDiasAulaRestantesNecessariosPorDisciplina < 0 ? entry.getValue() : entry.getValue() - quantidadeDiasAulaRestantesNecessariosPorDisciplina);

                                disciplinaMelhorAproveitamentoPrecisaVariosDiasDiasSemana = disciplinaPrecisaVariosDiasSemana;
                            }
                        }
                    }//fim for diasemana

                    if (cronogramaDisciplinaMelhorAproveitamento != null) {

                        adicionarDisciplina(cronogramaDisciplinasPorFase, cronogramaDisciplinaMelhorAproveitamento);

                        atualizarQuantidadeDiasAula(quantidadeAulasPorDiaDaSemanaAuxiliar,
                                disciplinasComDiasAulaNecessariosPorFase,
                                cronogramaDisciplinaMelhorAproveitamento);
                    }

                     existeConflito =
                            disciplinasComDiasAulaNecessariosPorFase.get(entry.getKey()) > 0 &&
                            !disciplinaMelhorAproveitamentoPrecisaVariosDiasDiasSemana;

                    if (existeConflito) {

                        final Boolean nivelVerificado = buscarDisciplinaComDiaSemanaConflitante(
                                cronogramaDisciplinasPorFase,
                                entry.getKey(),
                                disciplinasComDiasSemanaConflitantes,
                                nivelConflito,
                                disciplinasConflitantesVerificadas);


                        if (nivelVerificado == null) {//se o conflito for de fases
                            cronogramaDisciplinasPorFase.forEach(aa -> System.out.println("Fase: " + aa.getFaseId() + " " +
                                    aa.getDisciplina().getNome() + " " +
                                    aa.getDisciplina().getCargaHoraria() + "\n" +
                                    "OP" + aa.getOrdemPrioridadePorDiaSemana() + " " +
                                    "QTD" + aa.getQuantidadeDiasAula() + " " +
                                    aa.getDiaSemanaEnum() + " " +
                                    aa.getDisciplina().getProfessor().getNomeCompleto() + "\n" +
                                    aa.getDisciplina().getProfessor().getDiasSemanaDisponivel().stream().map(DiaSemanaDisponivel::getDiaSemanaEnum).toList() + "\n"
                            ));

                            System.out.println("Fase:" + entry.getKey().getFase().getId() + " " + entry.getKey().getNome() + " " + entry.getKey().getCargaHoraria());
                            System.out.println(entry.getKey().getProfessor().getNomeCompleto());
                            entry.getKey().getProfessor().getDiasSemanaDisponivel().forEach(aa -> System.out.println(aa.getDiaSemanaEnum()));
                            System.out.println("\n");

                            disciplinasConflitantesVerificadas.stream()
                                    .filter(a -> a.nivelConflito() == 1).toList().forEach(System.out::println);
                            throw new RuntimeException("conflito");
                        }
                        //AQUI VAI SER REINICIADO AS FASE CASO GERAR CONFLITO POR CAUSA DE OUTRA FASE

                        //TALVEZ AQUI SE NIVEL CONFLITO FOR 0 CRIAR LOGICA PARA DIAS SUGESTIVOS


                        nivelConflito = nivelVerificado ? nivelConflito - 2 : nivelConflito;
                        break;

//                        return gerarCronogramaPorFase(
//                                disciplinasComDiasAulaNecessariosPorCurso,
//                                fases,
//                                quantidadeAulasPorDiaDaSemana,
//                                disciplinasComDiasSemanaConflitantes,
//                                nivelVerificado ? nivelConflito - 2 : nivelConflito,
//                                disciplinasConflitantesVerificadas);
                    }
                }//fim while
                if (existeConflito){
                    break;
                }
            }//fim map
            if (existeConflito){
                break;
            }
        }

        if(existeConflito){
            continue;
        }

//        atualizarOrdemDePrioridadePorDiaSemana(cronogramaDisciplinasPorFase);//talvez criar verificacao para desempenho

        return cronogramaDisciplinasPorFase;

        }

        return null;
    }

    private boolean validarExisteConflitoFases(List<CronogramaDisciplinaDom> cronogramaDisciplinasPorFase,
                                               final Map<DiaSemanaEnum,Double> quantidadeAulasPorDiaSemanaOriginal,
                                               Disciplina disciplina,
                                               final DiaSemanaEnum diaSemanaEnum,
                                               double quantidadeDiasAula,
                                               final boolean disciplinaPrecisaVariosDiasSemana)
    {

        final double quantidadeAulasOcupadasNoDiaSemana = cronogramaDisciplinasPorFase.stream()
                .filter(cronogramaDisciplinaDom ->
                        cronogramaDisciplinaDom.getDisciplina().getProfessor().getId().equals(disciplina.getProfessor().getId()) &&
                        cronogramaDisciplinaDom.getDiaSemanaEnum().equals(diaSemanaEnum))
                .map(CronogramaDisciplinaDom::getQuantidadeDiasAula)
                .mapToDouble(Double::doubleValue)
                .sum();

       final double quantidadeAulasRestantesNoDiaSemana =
               quantidadeAulasPorDiaSemanaOriginal.get(diaSemanaEnum) - quantidadeAulasOcupadasNoDiaSemana;

       return quantidadeDiasAula > quantidadeAulasRestantesNoDiaSemana && !disciplinaPrecisaVariosDiasSemana;
    }

    private void atualizarOrdemDePrioridadePorDiaSemana(List<CronogramaDisciplinaDom> cronogramaDisciplinasPorFase){

        Map<DiaSemanaEnum, List<CronogramaDisciplinaDom>> cronogramaDisciplinasPorFaseSeparadoPorDiaSemanaEnum  = cronogramaDisciplinasPorFase.stream()
                .collect(Collectors.groupingBy(CronogramaDisciplinaDom::getDiaSemanaEnum));

        for(DiaSemanaEnum diaSemanaEnum : cronogramaDisciplinasPorFaseSeparadoPorDiaSemanaEnum.keySet()){
            for (int x = 1 ; x < cronogramaDisciplinasPorFaseSeparadoPorDiaSemanaEnum.get(diaSemanaEnum).size() ; x++){

               final double quantidadeDiasAulaAnterior =
                        cronogramaDisciplinasPorFaseSeparadoPorDiaSemanaEnum.get(diaSemanaEnum).get(x - 1).getQuantidadeDiasAula();
               final double quantidadeDiasAulaAtual =
                        cronogramaDisciplinasPorFaseSeparadoPorDiaSemanaEnum.get(diaSemanaEnum).get(x).getQuantidadeDiasAula();

                if(quantidadeDiasAulaAnterior < 5){
                    if(quantidadeDiasAulaAtual > quantidadeDiasAulaAnterior){
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

    private void adicionarDisciplina(List<CronogramaDisciplinaDom> cronogramaDisciplinasPorFase,CronogramaDisciplinaMelhorAproveitamentoDom cronogramaDisciplinaMelhorAproveitamento){

        int ordemPrioridadePorDiaSemana = 1;

        for (CronogramaDisciplinaDom cronogramaDisciplinaDom : cronogramaDisciplinasPorFase){
            if(
              cronogramaDisciplinaDom.getDiaSemanaEnum().equals(cronogramaDisciplinaMelhorAproveitamento.getDiaSemanaEnum()) &&
              cronogramaDisciplinaDom.getFaseId().equals(cronogramaDisciplinaMelhorAproveitamento.getDisciplina().getFase().getId()) &&
              cronogramaDisciplinaDom.getOrdemPrioridadePorDiaSemana() == ordemPrioridadePorDiaSemana
            ){
              ordemPrioridadePorDiaSemana++;
            }
        }

        cronogramaDisciplinasPorFase.add(new CronogramaDisciplinaDom(
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

    private Boolean buscarDisciplinaComDiaSemanaConflitante(List<CronogramaDisciplinaDom> cronogramaDisciplinasPorFase,
                                                            Disciplina disciplina,
                                                            Stack<CronogramaDisciplinaConflitanteDom> disciplinasComDiasSemanaConflitantes,
                                                            int nivelConflito,
                                                            List<CronogramaDisciplinaConflitanteDom> disciplinasConflitantesVerificadas)
    {

        if(nivelConflito == 0){
            return null;
        }

        for (CronogramaDisciplinaDom cronogramaDisciplina : cronogramaDisciplinasPorFase) {
          for (DiaSemanaDisponivel diaSemanaDisponivel : disciplina.getProfessor().getDiasSemanaDisponivel()){
              if(cronogramaDisciplina.getDiaSemanaEnum().equals(diaSemanaDisponivel.getDiaSemanaEnum())){
                  if(cronogramaDisciplina.getDisciplina().getProfessor().getDiasSemanaDisponivel().size() > 1) {

                      final boolean disciplinaConflitanteEstaVerificada = disciplinasConflitantesVerificadas.stream()
                              .anyMatch(disciplinaConflitanteVerificada ->
                              disciplinaConflitanteVerificada.disciplina().equals(disciplina) &&
                              disciplinaConflitanteVerificada.disciplinaConflitante().equals(cronogramaDisciplina.getDisciplina()) &&
                              disciplinaConflitanteVerificada.diaSemanaDisponivelConflitante().getDiaSemanaEnum().equals(cronogramaDisciplina.getDiaSemanaEnum()) &&
                              disciplinaConflitanteVerificada.nivelConflito() == nivelConflito);

                      if (disciplinaConflitanteEstaVerificada){
                          continue;
                      }

                      CronogramaDisciplinaConflitanteDom cronogramaDisciplinaConflitanteDom = new CronogramaDisciplinaConflitanteDom(
                              disciplina,
                              cronogramaDisciplina.getDisciplina(),
                              diaSemanaDisponivel,
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
                    }
                }
            }
        }
    }

    private boolean validarExisteMelhorAproveitamentoDias(final double disciplinaPorcentagemOcupacaoDiasAulaPorDiaSemana,
                                                          final double quantidadeDiasAulaRestantesPorDiaSemana,
                                                          CronogramaDisciplinaMelhorAproveitamentoDom cronogramaDisciplinaMelhorAproveitamento,
                                                          List<CronogramaDisciplinaDom> cronogramaDisciplinasPorFase,
                                                          final DiaSemanaEnum diaSemanaEnumAtual)
    {
        boolean existeMelhorAproveitamentoDias = true;

        if (cronogramaDisciplinaMelhorAproveitamento != null){
            existeMelhorAproveitamentoDias = false;

            final boolean existeDisciplinaNoDiaSemanaEnumAtual =
                    cronogramaDisciplinasPorFase.stream().anyMatch(cronogramaDisciplina -> cronogramaDisciplina.getDiaSemanaEnum().equals(diaSemanaEnumAtual));

            final boolean existeDisciplinaNoDiaSemanaEnumSalvo =
                    cronogramaDisciplinasPorFase.stream().anyMatch(cronogramaDisciplina -> cronogramaDisciplina.getDiaSemanaEnum().equals(cronogramaDisciplinaMelhorAproveitamento.getDiaSemanaEnum()));

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
            System.out.println("Apenas um periodo pode estar ativo");
        } else if (periodoEncontrado.isEmpty()) {
            System.out.println("É necessario ter um periodo ativo");
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

    private Map<Long,Map<Disciplina, Double>> buscarDisciplinasComDiasAulaNecessariosPorCurso(List<Fase> fases,Long cursoId){
        Set<Disciplina> disciplinasEncontradas =
                disciplinaRepository.findByCursoId(cursoId).get();//faseId

        Map<Disciplina, Double> disciplinasComDiasAulaNecessariosPorPeriodo = new HashMap<>();
        Map<Long,Map<Disciplina, Double>> disciplinasComDiasAulaNecessariosPorCurso = new LinkedHashMap<>();

        Long professorContratandoId = -1L;
        for (Disciplina disciplina : disciplinasEncontradas){

            criarNovaInstanciaProfessor(disciplina);
            professorContratandoId = verificarDisciplinaNaoPossuiProfessor(professorContratandoId,disciplina);

            final boolean existeDisciplinaExtensao = disciplinasEncontradas.stream()
                .filter(disciplinaFase -> disciplinaFase.getFase().getId().equals(disciplina.getFase().getId()))
                .anyMatch(disciplinaFase -> disciplinaFase.getExtensaoBooleanEnum().equals(BooleanEnum.SIM));

            if(existeDisciplinaExtensao){
//                removerDiaDaSemanaDesnecessarioProfessor(disciplina);
                reoordenarDiaDaSemanaProfessor(disciplina);
            }

           final double QUANTIDADE_DIAS_AULA_NECESSARIOS_POR_DISCIPLINA =
                    Math.floor(disciplina.getCargaHoraria() / disciplina.getCargaHorariaDiaria());

            disciplinasComDiasAulaNecessariosPorPeriodo
                    .put(disciplina,QUANTIDADE_DIAS_AULA_NECESSARIOS_POR_DISCIPLINA);
        }

        for (Fase fase :fases){

            Map<Disciplina, Double> disciplinasComDiasAulaNecessariosPorFase = disciplinasComDiasAulaNecessariosPorPeriodo.entrySet().stream()
                    .filter(entry -> entry.getKey().getFase().getId().equals(fase.getId()))
                    .sorted(Comparator.comparing((Map.Entry<Disciplina, Double> entry) -> entry.getKey().getProfessor().getDiasSemanaDisponivel().size())
                            .thenComparing(entry -> entry.getKey().getExtensaoBooleanEnum().equals(BooleanEnum.NAO))
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

    private void removerDiaDaSemanaDesnecessarioProfessor(Disciplina disciplina){
        if (disciplina.getExtensaoBooleanEnum().equals(BooleanEnum.SIM)){
            disciplina.getProfessor().getDiasSemanaDisponivel()
                    .removeIf(diaSemanaDisponivel -> !diaSemanaDisponivel.getDiaSemanaEnum().equals(DiaSemanaEnum.SABADO));
        } else {
            disciplina.getProfessor().getDiasSemanaDisponivel()
                    .removeIf(diaSemanaDisponivel -> diaSemanaDisponivel.getDiaSemanaEnum().equals(DiaSemanaEnum.SABADO));
        }
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
            professorNovaInstancia.setDiasSemanaDisponivel(diasSemanaDisponiveis);
            professorNovaInstancia.setStatusEnum(disciplina.getProfessor().getStatusEnum());
            professorNovaInstancia.setUsuario(disciplina.getProfessor().getUsuario());
            professorNovaInstancia.setDisciplinas(disciplina.getProfessor().getDisciplinas());

            disciplina.setProfessor(professorNovaInstancia);
        }
    }

    private void reoordenarDiaDaSemanaProfessor(Disciplina disciplina){
        Set<DiaSemanaDisponivel> diaSemanaDisponivelOrdenado;
        if (disciplina.getExtensaoBooleanEnum().equals(BooleanEnum.SIM)){
            diaSemanaDisponivelOrdenado = disciplina.getProfessor().getDiasSemanaDisponivel().stream()
                    .sorted(Comparator.comparing(diaSemanaDisponivel -> !diaSemanaDisponivel.getDiaSemanaEnum().equals(DiaSemanaEnum.SABADO)))
                    .collect(Collectors.toCollection(LinkedHashSet::new));

        } else {
            diaSemanaDisponivelOrdenado = disciplina.getProfessor().getDiasSemanaDisponivel().stream()
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

    private boolean verificarDiaSemanaEnumLiberado(final boolean naoExisteDisciplinaExtensao,final DiaSemanaEnum diaSemanaEnum, Disciplina disciplina, List<CronogramaDisciplinaDom> cronogramaDisciplinasPorFase){

        if(naoExisteDisciplinaExtensao){
            return true;
        }

        if (disciplina.getExtensaoBooleanEnum().equals(BooleanEnum.SIM) && !diaSemanaEnum.equals(DiaSemanaEnum.SABADO)){
            final boolean diaSemanaEnumLiberadoDiferenteSabado = cronogramaDisciplinasPorFase.stream()
                    .anyMatch(cronogramaDisciplina ->
                            cronogramaDisciplina.getFaseId().equals(disciplina.getFase().getId()) &&
                            cronogramaDisciplina.getDisciplina().equals(disciplina) &&
                            cronogramaDisciplina.getDiaSemanaEnum().equals(DiaSemanaEnum.SABADO));

            return diaSemanaEnumLiberadoDiferenteSabado;
        } else if (disciplina.getExtensaoBooleanEnum().equals(BooleanEnum.NAO) && diaSemanaEnum.equals(DiaSemanaEnum.SABADO)) {
            final boolean diaSemanaEnumLiberadoSerSabado = cronogramaDisciplinasPorFase.stream()
                    .anyMatch(cronogramaDisciplina ->
                            cronogramaDisciplina.getFaseId().equals(disciplina.getFase().getId()) &&
                            cronogramaDisciplina.getDisciplina().equals(disciplina) &&
                            !cronogramaDisciplina.getDiaSemanaEnum().equals(DiaSemanaEnum.SABADO));

            return diaSemanaEnumLiberadoSerSabado;
        }

        return true;
    }

    private void validarDiasDaSemanaNecessarios(Long cursoId){
//       Set<Fase> fasesEncontradas = faseRepository.buscarFasesPorCursoOndeExisteProfessorId(cursoId).get();
//       Map<Fase,Set<DiaSemanaEnum>> diasDaSemanaFaltantesPorFase =  new HashMap<>();
//
//       //REVER LOGICA validar por quantidade baseado na quantidade de disciplinas cadastradas
//       for(Fase fase: fasesEncontradas){//VERIFICAR ESSA VALIDACAO
//           Set<DiaSemanaEnum> diasSemanaEnum = disciplinaRepository.
//                   buscarDiasDaSemanaQuePossuemProfessorPorDisciplinas(cursoId, fase.getId()).get();
//
//           if(diasSemanaEnum.size() < 6){
//               Set<DiaSemanaEnum> diasDaSemanaFaltantes = Arrays.stream(DiaSemanaEnum.values())
//                       .filter(diaSemanaEnum -> diaSemanaEnum != DiaSemanaEnum.DOMINGO)
//                       .filter(diaSemanaEnum -> (!diasSemanaEnum.contains(diaSemanaEnum)))
//                       .collect(Collectors.toSet());
//
//               diasDaSemanaFaltantesPorFase.put(fase,diasDaSemanaFaltantes);
//           }
//       }
//
//       if (!diasDaSemanaFaltantesPorFase.isEmpty()){
//           for(Map.Entry<Fase,Set<DiaSemanaEnum>> entry : diasDaSemanaFaltantesPorFase.entrySet()){
//               System.out.println("curso " + cursoId + " " + entry.getKey() + "º fase Dias faltantes:" + entry.getValue());
//           }
//       }

       //TEM QUE DISPARAR A EXCEPTION COM BASE NA LISTA diasDaSemanaFaltantesPorFase
    }
}
