package com.cronograma.api.useCases.cronograma;

import com.cronograma.api.entitys.*;
import com.cronograma.api.entitys.enums.DiaSemanaEnum;
import com.cronograma.api.entitys.enums.StatusEnum;
import com.cronograma.api.useCases.cronograma.domains.CronogramaDisciplinaDetalhadoDom;
import com.cronograma.api.useCases.cronograma.domains.CronogramaDisciplinaDom;
import com.cronograma.api.useCases.cronograma.domains.CronogramaRequestDom;
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


    public List<CronogramaDisciplinaDom> gerarCronograma(CronogramaRequestDom cronograma){

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

        //validacao da quantidade de dias disponiveis por periodo X disciplinas + data exececao

        validarDiasDaSemanaNecessarios(cronograma.cursoId()); // fora do loop

        Map<DiaSemanaEnum,Double> quantidadeAulasPorDiaDaSemana =
                buscarQuantidadeAulasDisponveisPorDiaDaSemana(periodoAtivo.getDataInicial(), periodoAtivo.getDataFinal());

        //FILTRAR AS DATAS DE EXCECAO quantidadeAulasPorDiaDaSemana

        //LOOP FASE INICIO
        Map<Disciplina,DiaSemanaEnum> disciplinasComDiasSemanaConflitantes = new HashMap<>();

        List<CronogramaDisciplinaDom> cronogramaFaseOficial =
                gerarCronogramaPorFase(quantidadeAulasPorDiaDaSemana,disciplinasComDiasSemanaConflitantes);

        for(CronogramaDisciplinaDom cronogramaDisciplina : cronogramaFaseOficial){
            System.out.println(cronogramaDisciplina.getDisciplina().getNome() + " "
                    + cronogramaDisciplina.getDisciplina().getCargaHoraria() + " "
                    + cronogramaDisciplina.getDiaSemanaEnum());
            System.out.println("ORIGINAL " + quantidadeAulasPorDiaDaSemana.get(cronogramaDisciplina.getDiaSemanaEnum()));
            cronogramaDisciplina.getDisciplina().getProfessor().getDiasSemanaDisponivel()
                    .stream()
                    .forEach(diaSemanaDisponivel -> System.out.println(diaSemanaDisponivel.getDiaSemanaEnum()));
            System.out.println("\n");
        }

        return cronogramaFaseOficial;
    }

    private List<CronogramaDisciplinaDom> gerarCronogramaPorFase(Map<DiaSemanaEnum,Double> quantidadeAulasPorDiaDaSemana, Map<Disciplina,DiaSemanaEnum> disciplinasComDiasSemanaConflitantes) {
        Map<DiaSemanaEnum,Double> quantidadeAulasPorDiaDaSemanaAuxiliar = new HashMap<>(quantidadeAulasPorDiaDaSemana);

        Map<Disciplina, Double> disciplinasComDiasAulaNecessariosPorPeriodo =
                buscarDisciplinasComDiasAulaNecessariosPorPeriodo(2L);

        removerDiasDaSemanaConflitantes(disciplinasComDiasAulaNecessariosPorPeriodo,disciplinasComDiasSemanaConflitantes);

        List<CronogramaDisciplinaDom> cronogramaFaseOficial = new ArrayList<>();

        for (Map.Entry<Disciplina, Double> entry : disciplinasComDiasAulaNecessariosPorPeriodo.entrySet()) {
            //SE 38 > 20 PARCELADO = TRUE
            while (entry.getValue() > 0){ //enquanto precisar de dias a disciplina
                CronogramaDisciplinaDetalhadoDom cronogramaDisciplinaDetalhado = null;
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

                    if (QUANTIDADE_DIAS_AULA_NECESSARIOS_POR_DISCIPLINA > 0 && (QUANTIDADE_DIAS_AULA_NECESSARIOS_POR_DISCIPLINA <= QUANTIDADE_DIAS_AULA_POR_DIA_SEMANA)) {

                        boolean existeMelhorAproveitamentoDias = validarExisteMelhorAproveitamentoDias
                                (DISCIPLINA_PORCENTAGEM_OCUPACAO_DIAS_AULA_POR_DIA_SEMANA,QUANTIDADE_DIAS_AULA_RESTANTES_POR_DIA_SEMANA, cronogramaDisciplinaDetalhado);

                        if(existeMelhorAproveitamentoDias){
                            cronogramaDisciplinaDetalhado =
                                    new CronogramaDisciplinaDetalhadoDom(
                                            entry.getKey(),
                                            DIA_SEMANA_ENUM,
                                            QUANTIDADE_DIAS_AULA_RESTANTES_POR_DIA_SEMANA,
                                            QUANTIDADE_DIAS_AULA_RESTANTES_NECESSARIOS_POR_DISCIPLINA,
                                            DISCIPLINA_PORCENTAGEM_OCUPACAO_DIAS_AULA_POR_DIA_SEMANA);
                        }
                    }
                    //PRECISO VERIFICAR QUAL QUE FICOU FORA E FAZER UMA BLACKLIST
                }//fim for diasemana

                if(cronogramaDisciplinaDetalhado != null){

                    cronogramaFaseOficial
                            .add(new CronogramaDisciplinaDom
                                    (cronogramaDisciplinaDetalhado.getDisciplina(),
                                            cronogramaDisciplinaDetalhado.getDiaSemanaEnum()));

                    quantidadeAulasPorDiaDaSemanaAuxiliar
                            .put(cronogramaDisciplinaDetalhado.getDiaSemanaEnum(),
                                    (cronogramaDisciplinaDetalhado.getQuantidadeDiasAulaRestantesPorDiaSemana() < 0) ? 0.0 : cronogramaDisciplinaDetalhado.getQuantidadeDiasAulaRestantesPorDiaSemana());

                    disciplinasComDiasAulaNecessariosPorPeriodo
                            .put(cronogramaDisciplinaDetalhado.getDisciplina(),
                                    (cronogramaDisciplinaDetalhado.getQuantidadeDiasAulaRestantesNecessariosPorDisciplina() < 0) ? 0.0 : cronogramaDisciplinaDetalhado.getQuantidadeDiasAulaRestantesNecessariosPorDisciplina());
                }

                //VALIDAR SE ELE EXISTE NA LISTA OFICIAL
                final boolean EXISTE_CONFLITO = disciplinasComDiasAulaNecessariosPorPeriodo.get(entry.getKey()) > 0;

                if(EXISTE_CONFLITO){
                    buscarDisciplinaComDiaSemanaConflitante(cronogramaFaseOficial,entry.getKey(),disciplinasComDiasSemanaConflitantes);
                    return gerarCronogramaPorFase(quantidadeAulasPorDiaDaSemana,disciplinasComDiasSemanaConflitantes);
                }

                // PARCELADO = false
            }//fim while
        }//fim map

        return cronogramaFaseOficial;
    }

    private  void buscarDisciplinaComDiaSemanaConflitante(List<CronogramaDisciplinaDom> cronogramaFaseOficial, Disciplina disciplina, Map<Disciplina,DiaSemanaEnum> disciplinasComDiasSemanaConflitantes){
        for (CronogramaDisciplinaDom cronogramaDisciplina : cronogramaFaseOficial) {
          for (DiaSemanaDisponivel diaSemanaDisponivel : disciplina.getProfessor().getDiasSemanaDisponivel()){
              if(cronogramaDisciplina.getDiaSemanaEnum().equals(diaSemanaDisponivel.getDiaSemanaEnum())){
                  if(cronogramaDisciplina.getDisciplina().getProfessor().getDiasSemanaDisponivel().size() > 1) {
                      disciplinasComDiasSemanaConflitantes.put(cronogramaDisciplina.getDisciplina(),cronogramaDisciplina.getDiaSemanaEnum());
                      return;
                  }
              }
          }
        }
        //adicionar return se nao existir
    }

    private void removerDiasDaSemanaConflitantes(Map<Disciplina, Double> disciplinasComDiasAulaNecessariosPorPeriodo,Map<Disciplina,DiaSemanaEnum> disciplinasComDiasSemanaConflitantes){
        for (Map.Entry<Disciplina, Double> disciplinas : disciplinasComDiasAulaNecessariosPorPeriodo.entrySet()){
            for (Map.Entry<Disciplina,DiaSemanaEnum> disciplinaConflitantes : disciplinasComDiasSemanaConflitantes.entrySet()){
                if (disciplinas.getKey().equals(disciplinaConflitantes.getKey())){
                    disciplinas.getKey().getProfessor().getDiasSemanaDisponivel()
                            .removeIf(diaSemanaDisponivel ->
                                    disciplinaConflitantes.getValue().equals(diaSemanaDisponivel.getDiaSemanaEnum()));
                }
            }
        }
    }

    private boolean validarExisteMelhorAproveitamentoDias(double DISCIPLINA_PORCENTAGEM_OCUPACAO_DIAS_AULA_POR_DIA_SEMANA,double QUANTIDADE_DIAS_AULA_RESTANTES_POR_DIA_SEMANA, CronogramaDisciplinaDetalhadoDom cronogramaDisciplinaDetalhado){
        boolean existeMelhorAproveitamentoDias = true;

        if (cronogramaDisciplinaDetalhado != null){
            existeMelhorAproveitamentoDias = false;

            if(cronogramaDisciplinaDetalhado.getQuantidadeDiasAulaRestantesNecessariosPorDisciplina() == 0){
                return existeMelhorAproveitamentoDias;
            }

            if(DISCIPLINA_PORCENTAGEM_OCUPACAO_DIAS_AULA_POR_DIA_SEMANA > 75.00 &&
                    QUANTIDADE_DIAS_AULA_RESTANTES_POR_DIA_SEMANA < cronogramaDisciplinaDetalhado.getQuantidadeDiasAulaRestantesPorDiaSemana() &&
                    cronogramaDisciplinaDetalhado.getDisciplinaPorcentagemOcupacaoDiasAulaPorDiaSemana() > 75.00){

                existeMelhorAproveitamentoDias = true;

            } else if (DISCIPLINA_PORCENTAGEM_OCUPACAO_DIAS_AULA_POR_DIA_SEMANA <= 75.00 &&
                    QUANTIDADE_DIAS_AULA_RESTANTES_POR_DIA_SEMANA > cronogramaDisciplinaDetalhado.getQuantidadeDiasAulaRestantesPorDiaSemana()){

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

    private Map<Disciplina, Double> buscarDisciplinasComDiasAulaNecessariosPorPeriodo(Long faseId){
        Set<Disciplina> disciplinasEncontradas =
                disciplinaRepository.buscarDisciplinasPorFaseIdOrdenandoPorQtdDiaDisponivelProfessor(faseId).get();//faseId

        Map<Disciplina, Double> disciplinasComDiasAulaNecessariosPorPeriodo = new LinkedHashMap<>();

        for (Disciplina disciplina : disciplinasEncontradas){
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

       for(Fase fase: fasesEncontradas){
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
               System.out.println(entry.getKey() + " Dias faltantes:" + entry.getValue());
           }
       }

       //TEM QUE DISPARAR A EXCEPTION COM BASE NA LISTA diasDaSemanaFaltantesPorFase
    }
}
