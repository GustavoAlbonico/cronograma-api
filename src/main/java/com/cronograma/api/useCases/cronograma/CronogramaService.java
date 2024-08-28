package com.cronograma.api.useCases.cronograma;

import com.cronograma.api.entitys.*;
import com.cronograma.api.entitys.enums.DiaSemanaEnum;
import com.cronograma.api.entitys.enums.StatusEnum;
import com.cronograma.api.useCases.cronograma.domains.*;
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


    public List<TesteResponseCronogramaDom> gerarCronograma(CronogramaRequestDom cronograma){

        //vai pegar a disciplina do professor X junto disso os dias que precisa essa disciplina
        //e os dias que esse professor tem disponivel com a quantidade de dias disponiveis pelo periodo

        //LEMBRETE ORDENAR A LISTA PELO PROFESSOR QUE TIVER MENOS DIAS DISPONIVEIS + AULA QUE TIVER MENOS DIAS

        //TALVEZ LOOP COM BASE NA QUANTIDADE DE DIAS
        //vai fazer uma subtração da quantidade de dia disponivel x quantidade da disciplina
        //e vai definir o dia da semana que sobra menos dias e adicionar esse dia + disciplina uma lista

        //se na proxima disciplina não tiver dia disponivel para ela entao vai pegar a lista de dia + disciplina e adiciona

        //se o dia escolhido no loop for igual a um dia que está na lista PENSAR
        // a uma listaBloqueados (nessa lista se cair vai ignorar e pular para o proximo)

        //verificar se existe conflito

        Periodo periodoAtivo = buscarPeriodoAtivoAtual(); // fora do loop
        ///AAAAAAAAAAAAAAAAAAAAA

        //validacao da quantidade de dias disponiveis por periodo X disciplinas + data exececao

        validarDiasDaSemanaNecessarios(cronograma.cursoId()); // fora do loop

        Map<DiaSemanaEnum,Double> quantidadeAulasPorDiaDaSemana = //filtrar od dias de excecao
                buscarQuantidadeAulasDisponveisPorDiaDaSemana(periodoAtivo.getDataInicial(), periodoAtivo.getDataFinal());

        //FILTRAR AS DATAS DE EXCECAO quantidadeAulasPorDiaDaSemana

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
                testeResponseCronogramaDom.disicplinaCargaHoraria() + " OP: " +
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

        final Map<DiaSemanaEnum,Double> QUANTIDADE_AULAS_POR_DIA_SEMANA_ORIGINAL = Map.copyOf(quantidadeAulasPorDiaDaSemana);

        adicionarDiasDaSemanaConflitantesVerificados(disciplinasComDiasAulaNecessariosPorPeriodoAuxiliar,disciplinasConflitantesVerificadas);
        removerDiasDaSemanaConflitantes(disciplinasComDiasAulaNecessariosPorPeriodoAuxiliar,disciplinasComDiasSemanaConflitantes);

        List<CronogramaDisciplinaDom> cronogramaDisciplinasPorFase = new ArrayList<>();

        for (Map.Entry<Disciplina, Double> entry : disciplinasComDiasAulaNecessariosPorPeriodoAuxiliar.entrySet()) {
            while (entry.getValue() > 0){ //enquanto precisar de dias a disciplina
                CronogramaDisciplinaMelhorAproveitamentoDom cronogramaDisciplinaMelhorAproveitamento = null;
                boolean disciplinaMelhorAproveitamentoPrecisaVariosDiasDiasSemana = false;
                for (DiaSemanaDisponivel diaSemanaDisponivel : entry.getKey().getProfessor().getDiasSemanaDisponivel()) {
                    final DiaSemanaEnum DIA_SEMANA_ENUM = diaSemanaDisponivel.getDiaSemanaEnum();

                    final double QUANTIDADE_DIAS_AULA_NECESSARIOS_POR_DISCIPLINA = entry.getValue();
                    final double QUANTIDADE_DIAS_AULA_POR_DIA_SEMANA = quantidadeAulasPorDiaDaSemanaAuxiliar.get(DIA_SEMANA_ENUM);

                    final double DISCIPLINA_PORCENTAGEM_OCUPACAO_DIAS_AULA_POR_DIA_SEMANA =
                            (QUANTIDADE_DIAS_AULA_NECESSARIOS_POR_DISCIPLINA / QUANTIDADE_DIAS_AULA_POR_DIA_SEMANA) * 100;

                    final double QUANTIDADE_DIAS_AULA_RESTANTES_NECESSARIOS_POR_DISCIPLINA =
                            QUANTIDADE_DIAS_AULA_NECESSARIOS_POR_DISCIPLINA - QUANTIDADE_DIAS_AULA_POR_DIA_SEMANA;

                    final double QUANTIDADE_DIAS_AULA_RESTANTES_POR_DIA_SEMANA =
                            QUANTIDADE_DIAS_AULA_POR_DIA_SEMANA - QUANTIDADE_DIAS_AULA_NECESSARIOS_POR_DISCIPLINA;

                    final boolean DISCIPLINA_PRECISA_VARIOS_DIAS_SEMANA =
                            QUANTIDADE_DIAS_AULA_NECESSARIOS_POR_DISCIPLINA > QUANTIDADE_AULAS_POR_DIA_SEMANA_ORIGINAL.get(DIA_SEMANA_ENUM) &&
                            QUANTIDADE_DIAS_AULA_RESTANTES_NECESSARIOS_POR_DISCIPLINA <= QUANTIDADE_DIAS_AULA_POR_DIA_SEMANA;

                    if (QUANTIDADE_DIAS_AULA_NECESSARIOS_POR_DISCIPLINA > 0 &&
                            (QUANTIDADE_DIAS_AULA_NECESSARIOS_POR_DISCIPLINA <= QUANTIDADE_DIAS_AULA_POR_DIA_SEMANA || DISCIPLINA_PRECISA_VARIOS_DIAS_SEMANA)) {

                        final boolean EXISTE_MELHOR_APROVEITAMENTO_DIAS =
                                validarExisteMelhorAproveitamentoDias(
                                        DISCIPLINA_PORCENTAGEM_OCUPACAO_DIAS_AULA_POR_DIA_SEMANA,
                                        QUANTIDADE_DIAS_AULA_RESTANTES_POR_DIA_SEMANA,
                                        cronogramaDisciplinaMelhorAproveitamento,
                                        cronogramaDisciplinasPorFase,
                                        DIA_SEMANA_ENUM);

                        if(EXISTE_MELHOR_APROVEITAMENTO_DIAS){
                            cronogramaDisciplinaMelhorAproveitamento =
                                    new CronogramaDisciplinaMelhorAproveitamentoDom(
                                            entry.getKey(),
                                            DIA_SEMANA_ENUM,
                                            QUANTIDADE_DIAS_AULA_RESTANTES_POR_DIA_SEMANA,
                                            QUANTIDADE_DIAS_AULA_RESTANTES_NECESSARIOS_POR_DISCIPLINA,
                                            DISCIPLINA_PORCENTAGEM_OCUPACAO_DIAS_AULA_POR_DIA_SEMANA);

                            disciplinaMelhorAproveitamentoPrecisaVariosDiasDiasSemana = DISCIPLINA_PRECISA_VARIOS_DIAS_SEMANA;
                        }
                    }
                }//fim for diasemana

                if(cronogramaDisciplinaMelhorAproveitamento != null){

                    adicionarDisciplina(cronogramaDisciplinasPorFase,cronogramaDisciplinaMelhorAproveitamento);

                    atualizarQuantidadeDiasAula(quantidadeAulasPorDiaDaSemanaAuxiliar,
                            disciplinasComDiasAulaNecessariosPorPeriodoAuxiliar,
                                               cronogramaDisciplinaMelhorAproveitamento);
                }

                    final boolean EXISTE_CONFLITO =
                            disciplinasComDiasAulaNecessariosPorPeriodoAuxiliar.get(entry.getKey()) > 0 &&
                            !disciplinaMelhorAproveitamentoPrecisaVariosDiasDiasSemana;

                    if(EXISTE_CONFLITO){
                        final boolean NIVEL_VERIFICADO = buscarDisciplinaComDiaSemanaConflitante(
                                cronogramaDisciplinasPorFase,
                                entry.getKey(),
                                disciplinasComDiasSemanaConflitantes,
                                nivelConflito,
                                disciplinasConflitantesVerificadas);

                        return gerarCronogramaPorFase(
                                disciplinasComDiasAulaNecessariosPorPeriodo,
                                quantidadeAulasPorDiaDaSemana,
                                disciplinasComDiasSemanaConflitantes,
                                NIVEL_VERIFICADO ? nivelConflito - 2 : nivelConflito,
                                disciplinasConflitantesVerificadas);
                        //ADICIONAR ordemPrioridadePorDiaSemana
                    }
            }//fim while
        }//fim map
        return cronogramaDisciplinasPorFase;
    }

    private void adicionarDisciplina(List<CronogramaDisciplinaDom> cronogramaDisciplinasPorFase,CronogramaDisciplinaMelhorAproveitamentoDom cronogramaDisciplinaMelhorAproveitamento){

        int ordemPrioridadePorDiaSemana = (int) cronogramaDisciplinasPorFase.stream()
                .filter(cronogramaDisciplinaDom ->
                        cronogramaDisciplinaDom.getDiaSemanaEnum()
                                .equals(cronogramaDisciplinaMelhorAproveitamento.getDiaSemanaEnum()))
                .count();

        cronogramaDisciplinasPorFase.add(new CronogramaDisciplinaDom(
                cronogramaDisciplinaMelhorAproveitamento.getDisciplina(),
                cronogramaDisciplinaMelhorAproveitamento.getDiaSemanaEnum(),
                ordemPrioridadePorDiaSemana + 1));
    }
    private void atualizarQuantidadeDiasAula (Map<DiaSemanaEnum,Double> quantidadeAulasPorDiaDaSemanaAuxiliar,
                                             Map<Disciplina, Double> disciplinasComDiasAulaNecessariosPorPeriodo,
                                             CronogramaDisciplinaMelhorAproveitamentoDom cronogramaDisciplinaMelhorAproveitamento)
    {
        final double QUANTIDADE_DIAS_AULA_RESTANTES_POR_DIA_SEMANA =
                (cronogramaDisciplinaMelhorAproveitamento.getQuantidadeDiasAulaRestantesPorDiaSemana() < 0) ?
                0.0 :
                cronogramaDisciplinaMelhorAproveitamento.getQuantidadeDiasAulaRestantesPorDiaSemana();

        final double QUANTIDADE_DIAS_AULA_RESTANTES_NECESSARIOS_POR_DISCIPLINA =
                (cronogramaDisciplinaMelhorAproveitamento.getQuantidadeDiasAulaRestantesNecessariosPorDisciplina() < 0) ?
                0.0 :
                cronogramaDisciplinaMelhorAproveitamento.getQuantidadeDiasAulaRestantesNecessariosPorDisciplina();

        quantidadeAulasPorDiaDaSemanaAuxiliar
                .put(cronogramaDisciplinaMelhorAproveitamento.getDiaSemanaEnum(), QUANTIDADE_DIAS_AULA_RESTANTES_POR_DIA_SEMANA);

        disciplinasComDiasAulaNecessariosPorPeriodo
                .put(cronogramaDisciplinaMelhorAproveitamento.getDisciplina(),QUANTIDADE_DIAS_AULA_RESTANTES_NECESSARIOS_POR_DISCIPLINA);
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

                      final boolean NIVEL_CONFLITO_VERIFICADO = disciplinasConflitantesVerificadas.stream()
                              .anyMatch(disciplinaConflitanteVerificada ->
                              disciplinaConflitanteVerificada.disciplina().equals(disciplina) &&
                              disciplinaConflitanteVerificada.disciplinaConflitante().equals(cronogramaDisciplina.getDisciplina()) &&
                              disciplinaConflitanteVerificada.diaSemanaDisponivelConflitante().getDiaSemanaEnum().equals(cronogramaDisciplina.getDiaSemanaEnum()) &&
                              disciplinaConflitanteVerificada.nivelConflito() == nivelConflito);

                      if (NIVEL_CONFLITO_VERIFICADO){
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

    private boolean validarExisteMelhorAproveitamentoDias(double DISCIPLINA_PORCENTAGEM_OCUPACAO_DIAS_AULA_POR_DIA_SEMANA,
                                                          double QUANTIDADE_DIAS_AULA_RESTANTES_POR_DIA_SEMANA,
                                                          CronogramaDisciplinaMelhorAproveitamentoDom cronogramaDisciplinaMelhorAproveitamento,
                                                          List<CronogramaDisciplinaDom> cronogramaDisciplinasPorFase,
                                                          DiaSemanaEnum DIA_SEMANA_ENUM_ATUAL)
    {
        boolean existeMelhorAproveitamentoDias = true;

        if (cronogramaDisciplinaMelhorAproveitamento != null){
            existeMelhorAproveitamentoDias = false;

            final boolean EXISTE_DISPLINA_NO_DIA_SEMANA_ENUM_ATUAL =
                    cronogramaDisciplinasPorFase.stream().anyMatch(cronogramaDisciplina -> cronogramaDisciplina.getDiaSemanaEnum().equals(DIA_SEMANA_ENUM_ATUAL));

            final boolean EXISTE_DISPLINA_NO_DIA_SEMANA_ENUM_SALVO =
                    cronogramaDisciplinasPorFase.stream().anyMatch(cronogramaDisciplina -> cronogramaDisciplina.getDiaSemanaEnum().equals(cronogramaDisciplinaMelhorAproveitamento.getDiaSemanaEnum()));

            if(
               DISCIPLINA_PORCENTAGEM_OCUPACAO_DIAS_AULA_POR_DIA_SEMANA > 75.00 &&
               QUANTIDADE_DIAS_AULA_RESTANTES_POR_DIA_SEMANA < cronogramaDisciplinaMelhorAproveitamento.getQuantidadeDiasAulaRestantesPorDiaSemana() &&
               (cronogramaDisciplinaMelhorAproveitamento.getDisciplinaPorcentagemOcupacaoDiasAulaPorDiaSemana() > 75.00 ||
                EXISTE_DISPLINA_NO_DIA_SEMANA_ENUM_ATUAL)
            ){
                existeMelhorAproveitamentoDias = true;
            } else if(EXISTE_DISPLINA_NO_DIA_SEMANA_ENUM_SALVO){
                return existeMelhorAproveitamentoDias;
            } else if (
                    DISCIPLINA_PORCENTAGEM_OCUPACAO_DIAS_AULA_POR_DIA_SEMANA <= 75.00 &&
                    QUANTIDADE_DIAS_AULA_RESTANTES_POR_DIA_SEMANA > cronogramaDisciplinaMelhorAproveitamento.getQuantidadeDiasAulaRestantesPorDiaSemana())
            {
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

    private Map<DiaSemanaEnum,Double> buscarQuantidadeAulasDisponveisPorDiaDaSemana(LocalDate dataInicial, LocalDate dataFinal){
        Map<DiaSemanaEnum,Double> quantidadeAulasPorDiaDaSemana = new HashMap<>();

        LocalDate dataInicialAuxiliar = dataInicial;
        while(dataInicialAuxiliar.isBefore(dataFinal.plusDays(1L))){
            DayOfWeek dayOfWeek = dataInicialAuxiliar.getDayOfWeek();
            if(!dayOfWeek.equals(DayOfWeek.SUNDAY)){
                DiaSemanaEnum diaDaSemana = DiaSemanaEnum.dayOfWeekParaDiaSemanaEnum(dayOfWeek);
                quantidadeAulasPorDiaDaSemana.merge(diaDaSemana, 1.0, Double::sum);
            }
            dataInicialAuxiliar = dataInicialAuxiliar.plusDays(1);
        }

        return quantidadeAulasPorDiaDaSemana;
    }

    private Map<Disciplina, Double> buscarDisciplinasComDiasAulaNecessariosPorPeriodo(Long cursoId,Long faseId){
        Set<Disciplina> disciplinasEncontradas =
                disciplinaRepository.buscarDisciplinasPorFaseIdOrdenandoPorQtdDiaDisponivelProfessor(cursoId,faseId).get();//faseId

        Map<Disciplina, Double> disciplinasComDiasAulaNecessariosPorPeriodo = new LinkedHashMap<>();

        for (Disciplina disciplina : disciplinasEncontradas){
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

           final double QUANTIDADE_DIAS_AULA_NECESSARIOS_POR_DISCIPLINA =
                    Math.floor(disciplina.getCargaHoraria() / disciplina.getCargaHorariaDiaria());

            disciplinasComDiasAulaNecessariosPorPeriodo
                    .put(disciplina,QUANTIDADE_DIAS_AULA_NECESSARIOS_POR_DISCIPLINA);
        }

        return disciplinasComDiasAulaNecessariosPorPeriodo;
    }

    private void validarDiasDaSemanaNecessarios(Long cursoId){
       Set<Fase> fasesEncontradas = faseRepository.buscarFasesPorCursoOndeExisteProfessorId(cursoId).get();
       Map<Fase,Set<DiaSemanaEnum>> diasDaSemanaFaltantesPorFase =  new HashMap<>();

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
