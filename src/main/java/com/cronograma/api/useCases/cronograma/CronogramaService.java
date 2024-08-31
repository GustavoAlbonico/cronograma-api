package com.cronograma.api.useCases.cronograma;

import com.cronograma.api.entitys.*;
import com.cronograma.api.entitys.enums.BooleanEnum;
import com.cronograma.api.entitys.enums.DiaSemanaEnum;
import com.cronograma.api.entitys.enums.StatusEnum;
import com.cronograma.api.useCases.cronograma.domains.*;
import com.cronograma.api.useCases.dataBloqueada.implement.repositorys.DataBloqueadaRepository;
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


    public List<TesteResponseCronogramaDom> gerarCronograma(CronogramaRequestDom cronograma){

        Periodo periodoAtivo = buscarPeriodoAtivoAtual(); // fora do loop

        //validarProfessorPossuiDiaSemanaDisponivel(cronograma.cursoId()); //CRIAR

        //validacao da quantidade de dias disponiveis por periodo X disciplinas + data exececao

//        validarDiasDaSemanaNecessarios(cronograma.cursoId()); // fora do loop //REVER


        List<DataBloqueada> datasBloqueadas = dataBloqueadaRepository.findAll();
        Map<DiaSemanaEnum,Double> quantidadeAulasPorDiaDaSemana = //filtrar data exececao
                buscarQuantidadeAulasDisponveisPorDiaDaSemana(periodoAtivo,datasBloqueadas);

        //LOOP FASE INICIO
        Stack<CronogramaDisciplinaConflitanteDom> disciplinasComDiasSemanaConflitantes = new Stack<>();
        List<CronogramaDisciplinaConflitanteDom> disciplinasConflitantesVerificadas =  new ArrayList<>();

        Map<Disciplina, Double> disciplinasComDiasAulaNecessariosPorPeriodo =
                buscarDisciplinasComDiasAulaNecessariosPorPeriodo(cronograma.cursoId(), cronograma.faseId());

        List<CronogramaDisciplinaDom> cronogramaFaseOficial =
                gerarCronogramaPorFase(
                        disciplinasComDiasAulaNecessariosPorPeriodo,
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
        .sorted(Comparator.comparing(TesteResponseCronogramaDom::diaSemanaEnum))
        .toList();

    for (TesteResponseCronogramaDom testeResponseCronogramaDom :response){
        System.out.println(
                testeResponseCronogramaDom.disciplinaNome() + " " +
                testeResponseCronogramaDom.disicplinaCargaHoraria() + "PRECISA: " +
                testeResponseCronogramaDom.quantidadeDiasAula() + " OP: " +
                testeResponseCronogramaDom.ordemPrioridadePorDiaSemana() + " " +
                testeResponseCronogramaDom.diaSemanaEnum() + " " +
                testeResponseCronogramaDom.nomeProfessor());
                testeResponseCronogramaDom.listaDiasSemanaDisponiveis().stream().forEach(
                        System.out::println
                );
        System.out.println("\n");
    }

        return response;
    }

    private List<CronogramaDisciplinaDom> gerarCronogramaPorFase(Map<Disciplina, Double> disciplinasComDiasAulaNecessariosPorPeriodo,
                                                                 Map<DiaSemanaEnum,Double> quantidadeAulasPorDiaDaSemana,
                                                                 Stack<CronogramaDisciplinaConflitanteDom> disciplinasComDiasSemanaConflitantes,
                                                                 int nivelConflito,
                                                                 List<CronogramaDisciplinaConflitanteDom> disciplinasConflitantesVerificadas)
    {
        nivelConflito++;

        Map<DiaSemanaEnum,Double> quantidadeAulasPorDiaDaSemanaAuxiliar = new HashMap<>(quantidadeAulasPorDiaDaSemana);
        Map<Disciplina, Double> disciplinasComDiasAulaNecessariosPorPeriodoAuxiliar = new LinkedHashMap<>(disciplinasComDiasAulaNecessariosPorPeriodo);

        final Map<DiaSemanaEnum,Double> quantidadeAulasPorDiaSemanaOriginal = Map.copyOf(quantidadeAulasPorDiaDaSemana);

        adicionarDiasDaSemanaConflitantesVerificados(disciplinasComDiasAulaNecessariosPorPeriodoAuxiliar,disciplinasConflitantesVerificadas);
        removerDiasDaSemanaConflitantes(disciplinasComDiasAulaNecessariosPorPeriodoAuxiliar,disciplinasComDiasSemanaConflitantes);

        List<CronogramaDisciplinaDom> cronogramaDisciplinasPorFase = new ArrayList<>();

        for (Map.Entry<Disciplina, Double> entry : disciplinasComDiasAulaNecessariosPorPeriodoAuxiliar.entrySet()) {
            while (entry.getValue() > 0){
                CronogramaDisciplinaMelhorAproveitamentoDom cronogramaDisciplinaMelhorAproveitamento = null;
                boolean disciplinaMelhorAproveitamentoPrecisaVariosDiasDiasSemana = false;
                for (DiaSemanaDisponivel diaSemanaDisponivel : entry.getKey().getProfessor().getDiasSemanaDisponivel()) {
                    final DiaSemanaEnum diaSemanaEnum = diaSemanaDisponivel.getDiaSemanaEnum();

                    final double quantidadeDiasAulaNecessariosPorDisciplina = entry.getValue();
                    final double quantidadeDiasAulaPorDiaSemana = quantidadeAulasPorDiaDaSemanaAuxiliar.get(diaSemanaEnum);

                    final double disciplinaPorcentagemOcupacaoDiasAulaPorDiaSemana =
                            (quantidadeDiasAulaNecessariosPorDisciplina / quantidadeDiasAulaPorDiaSemana) * 100;

                    final double quantidadeDiasAulaRestantesNecessariosPorDisciplina =
                            quantidadeDiasAulaNecessariosPorDisciplina - quantidadeDiasAulaPorDiaSemana;

                    final double quantidadeDiasAulaRestantesPorDiaSemana =
                            quantidadeDiasAulaPorDiaSemana - quantidadeDiasAulaNecessariosPorDisciplina;

                    final boolean disciplinaPrecisaVariosDiasSemana =
                            quantidadeDiasAulaNecessariosPorDisciplina > quantidadeAulasPorDiaSemanaOriginal.get(diaSemanaEnum) &&
                                    quantidadeDiasAulaRestantesNecessariosPorDisciplina <= quantidadeDiasAulaPorDiaSemana;

                    if (quantidadeDiasAulaNecessariosPorDisciplina > 0 &&
                            (quantidadeDiasAulaNecessariosPorDisciplina <= quantidadeDiasAulaPorDiaSemana || disciplinaPrecisaVariosDiasSemana)) {

                        final boolean existeMelhorAproveitamentoDias =
                                validarExisteMelhorAproveitamentoDias(
                                        disciplinaPorcentagemOcupacaoDiasAulaPorDiaSemana,
                                        quantidadeDiasAulaRestantesPorDiaSemana,
                                        cronogramaDisciplinaMelhorAproveitamento,
                                        cronogramaDisciplinasPorFase,
                                        diaSemanaEnum);

                        if(existeMelhorAproveitamentoDias){
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

                if(cronogramaDisciplinaMelhorAproveitamento != null){

                    adicionarDisciplina(cronogramaDisciplinasPorFase,cronogramaDisciplinaMelhorAproveitamento);

                    atualizarQuantidadeDiasAula(quantidadeAulasPorDiaDaSemanaAuxiliar,
                                                disciplinasComDiasAulaNecessariosPorPeriodoAuxiliar,
                                                cronogramaDisciplinaMelhorAproveitamento);
                }

                    final boolean existeConflito =
                            disciplinasComDiasAulaNecessariosPorPeriodoAuxiliar.get(entry.getKey()) > 0 &&
                            !disciplinaMelhorAproveitamentoPrecisaVariosDiasDiasSemana;

                    if(existeConflito){
                        final boolean nivelVerificado = buscarDisciplinaComDiaSemanaConflitante(
                                cronogramaDisciplinasPorFase,
                                entry.getKey(),
                                disciplinasComDiasSemanaConflitantes,
                                nivelConflito,
                                disciplinasConflitantesVerificadas);

                        //TALVEZ AQUI SE NIVEL CONFLITO FOR 0 CRIAR LOGICA PARA DIAS SUGESTIVOS

                        return gerarCronogramaPorFase(
                                disciplinasComDiasAulaNecessariosPorPeriodo,
                                quantidadeAulasPorDiaDaSemana,
                                disciplinasComDiasSemanaConflitantes,
                                nivelVerificado ? nivelConflito - 2 : nivelConflito,
                                disciplinasConflitantesVerificadas);
                    }
            }//fim while
        }//fim map

        atualizarOrdemDePrioridadePorDiaSemana(cronogramaDisciplinasPorFase);//talvez criar verificacao para desempenho


        return cronogramaDisciplinasPorFase;
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

        int ordemPrioridadePorDiaSemana = (int) cronogramaDisciplinasPorFase.stream()
                .filter(cronogramaDisciplinaDom ->
                        cronogramaDisciplinaDom.getDiaSemanaEnum().equals(cronogramaDisciplinaMelhorAproveitamento.getDiaSemanaEnum()))
                .count();

        cronogramaDisciplinasPorFase.add(new CronogramaDisciplinaDom(
                cronogramaDisciplinaMelhorAproveitamento.getDisciplina(),
                cronogramaDisciplinaMelhorAproveitamento.getDiaSemanaEnum(),
                cronogramaDisciplinaMelhorAproveitamento.getQuantidadeDiasAula(),
                ordemPrioridadePorDiaSemana + 1));
    }

    private void atualizarQuantidadeDiasAula (Map<DiaSemanaEnum,Double> quantidadeAulasPorDiaDaSemanaAuxiliar,
                                              Map<Disciplina, Double> disciplinasComDiasAulaNecessariosPorPeriodo,
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

        disciplinasComDiasAulaNecessariosPorPeriodo
                .put(cronogramaDisciplinaMelhorAproveitamento.getDisciplina(),quantidadeDiasAulaRestantesNecessariosPorDisciplina);
    }

    private boolean buscarDisciplinaComDiaSemanaConflitante(List<CronogramaDisciplinaDom> cronogramaDisciplinasPorFase,
                                                            Disciplina disciplina,
                                                            Stack<CronogramaDisciplinaConflitanteDom> disciplinasComDiasSemanaConflitantes,
                                                            int nivelConflito,
                                                            List<CronogramaDisciplinaConflitanteDom> disciplinasConflitantesVerificadas)
    {
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

        if(nivelConflito == 0){
            System.out.println(disciplina.getNome() + " " + disciplina.getCargaHoraria());
            disciplina.getProfessor().getDiasSemanaDisponivel().stream().forEach(a -> System.out.println(a.getDiaSemanaEnum()));
            throw new RuntimeException("Conflito");
        }

        if (!disciplinasComDiasSemanaConflitantes.isEmpty()){
            disciplinasComDiasSemanaConflitantes.pop();
        }

        return true;
    }

    private void removerDiasDaSemanaConflitantes(Map<Disciplina, Double> disciplinasComDiasAulaNecessariosPorPeriodo,
                                                 Stack<CronogramaDisciplinaConflitanteDom> disciplinasComDiasSemanaConflitantes)
    {
        for (Map.Entry<Disciplina, Double> disciplinas : disciplinasComDiasAulaNecessariosPorPeriodo.entrySet()){
            for (CronogramaDisciplinaConflitanteDom disciplinaConflitantes : disciplinasComDiasSemanaConflitantes){
                if (disciplinas.getKey().equals(disciplinaConflitantes.disciplinaConflitante())){
                    disciplinas.getKey().getProfessor().getDiasSemanaDisponivel()
                            .removeIf(diaSemanaDisponivel ->
                                    disciplinaConflitantes.diaSemanaDisponivelConflitante().getDiaSemanaEnum().equals(diaSemanaDisponivel.getDiaSemanaEnum()));
                }
            }
        }
    }

    private void adicionarDiasDaSemanaConflitantesVerificados(Map<Disciplina, Double> disciplinasComDiasAulaNecessariosPorPeriodo,  List<CronogramaDisciplinaConflitanteDom> disciplinasConflitantesVerificadas) {

        for (Map.Entry<Disciplina, Double> disciplinas : disciplinasComDiasAulaNecessariosPorPeriodo.entrySet()){
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

    private Map<Disciplina, Double> buscarDisciplinasComDiasAulaNecessariosPorPeriodo(Long cursoId,Long faseId){
        Set<Disciplina> disciplinasEncontradas =
                disciplinaRepository.buscarDisciplinasPorFaseIdOrdenandoPorQtdDiaDisponivelProfessor(cursoId,faseId).get();//faseId

        Map<Disciplina, Double> disciplinasComDiasAulaNecessariosPorPeriodo = new HashMap<>();

        for (Disciplina disciplina : disciplinasEncontradas){

            criarNovaInstanciaProfessor(disciplina);
            verificarDisciplinaNaoPossuiProfessor(disciplina);
            verificarDisciplinaExtensao(disciplina);

           final double QUANTIDADE_DIAS_AULA_NECESSARIOS_POR_DISCIPLINA =
                    Math.floor(disciplina.getCargaHoraria() / disciplina.getCargaHorariaDiaria());

            disciplinasComDiasAulaNecessariosPorPeriodo
                    .put(disciplina,QUANTIDADE_DIAS_AULA_NECESSARIOS_POR_DISCIPLINA);
        }

        return disciplinasComDiasAulaNecessariosPorPeriodo.entrySet().stream()
                .sorted(Comparator.comparing((Map.Entry<Disciplina, Double> entry) -> entry.getKey().getProfessor().getDiasSemanaDisponivel().size())
                    .thenComparing(entry -> entry.getKey().getExtensaoBooleanEnum().equals(BooleanEnum.NAO))
                    .thenComparing(Comparator.comparing((Map.Entry<Disciplina, Double> entry) -> entry.getKey().getCargaHoraria()).reversed()))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));
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

    private void verificarDisciplinaNaoPossuiProfessor(Disciplina disciplina){
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
            contratando.setNomeCompleto("Contratando");
            contratando.setStatusEnum(StatusEnum.ATIVO);
            contratando.setDiasSemanaDisponivel(diasSemanaDisponiveis);

            disciplina.setProfessor(contratando);
        }
    }

    private void verificarDisciplinaExtensao(Disciplina disciplina){
        if (disciplina.getExtensaoBooleanEnum().equals(BooleanEnum.SIM)){
            disciplina.getProfessor().getDiasSemanaDisponivel()
                    .removeIf(diaSemanaDisponivel ->
                            !diaSemanaDisponivel.getDiaSemanaEnum().equals(DiaSemanaEnum.SABADO));

        }
    }

    private void validarDiasDaSemanaNecessarios(Long cursoId){
       Set<Fase> fasesEncontradas = faseRepository.buscarFasesPorCursoOndeExisteProfessorId(cursoId).get();
       Map<Fase,Set<DiaSemanaEnum>> diasDaSemanaFaltantesPorFase =  new HashMap<>();

       //REVER LOGICA validar por quantidade baseado na quantidade de disciplinas cadastradas
       for(Fase fase: fasesEncontradas){//VERIFICAR ESSA VALIDACAO
           Set<DiaSemanaEnum> diasSemanaEnum = disciplinaRepository.
                   buscarDiasDaSemanaQuePossuemProfessorPorDisciplinas(cursoId, fase.getId()).get();

           if(diasSemanaEnum.size() < 6){
               Set<DiaSemanaEnum> diasDaSemanaFaltantes = Arrays.stream(DiaSemanaEnum.values())
                       .filter(diaSemanaEnum -> diaSemanaEnum != DiaSemanaEnum.DOMINGO)
                       .filter(diaSemanaEnum -> (!diasSemanaEnum.contains(diaSemanaEnum)))
                       .collect(Collectors.toSet());

               diasDaSemanaFaltantesPorFase.put(fase,diasDaSemanaFaltantes);
           }
       }

       if (!diasDaSemanaFaltantesPorFase.isEmpty()){
           for(Map.Entry<Fase,Set<DiaSemanaEnum>> entry : diasDaSemanaFaltantesPorFase.entrySet()){
               System.out.println("curso " + cursoId + " " + entry.getKey() + "º fase Dias faltantes:" + entry.getValue());
           }
       }

       //TEM QUE DISPARAR A EXCEPTION COM BASE NA LISTA diasDaSemanaFaltantesPorFase
    }
}
