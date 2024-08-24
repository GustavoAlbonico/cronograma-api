package com.cronograma.api.useCases.cronograma;

import com.cronograma.api.entitys.*;
import com.cronograma.api.entitys.enums.DiaSemanaEnum;
import com.cronograma.api.entitys.enums.StatusEnum;
import com.cronograma.api.useCases.cronograma.domains.CronogramaDisciplinaDiasAulaDom;
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


    public Set<CronogramaDisciplinaDom> gerarCronograma(CronogramaRequestDom cronograma){

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

        Map<DiaSemanaEnum,Integer> quantidadeAulasPorDiaDaSemana =
                buscarQuantidadeAulasDisponveisPorDiaDaSemana(periodoAtivo.getDataInicial(), periodoAtivo.getDataFinal());

        //FILTRAR AS DATAS DE EXCECAO quantidadeAulasPorDiaDaSemana

        //LOOP FASE INICIO

        Set<CronogramaDisciplinaDiasAulaDom> disciplinasDiasAula =
                buscarQuantidadeAulasPorDisciplina(2L, quantidadeAulasPorDiaDaSemana);

        Set<CronogramaDisciplinaDom> cronogramaFaseOficial = new HashSet<>();

        for (CronogramaDisciplinaDiasAulaDom disciplinaDiasAula : disciplinasDiasAula) {
              DiaSemanaEnum diaSemanaEnum = disciplinaDiasAula.disciplina().getProfessor().getDiasSemanaDisponivel()
                        .stream()
                        .findFirst()
                        .get().getDiaSemanaEnum();

            for (double x = 0.0 ; x < Math.ceil(disciplinaDiasAula.disciplinaQuantidadeDiasAulaPorSemana().get(diaSemanaEnum)); x++){
                CronogramaDisciplinaDom cronogramaDisciplinaAntigo = new CronogramaDisciplinaDom();
                boolean primeiraVerificacao = true;
                double quantidadeDiasRestantesMelhorAproveitamento = 0.0;

                for(DiaSemanaDisponivel diaSemanaDisponivel : disciplinaDiasAula.disciplina().getProfessor().getDiasSemanaDisponivel()){
                    diaSemanaEnum = diaSemanaDisponivel.getDiaSemanaEnum();
                    final double PORCENTAGEM_DIAS_AULA_OCUPADOS =
                            disciplinaDiasAula.disciplinaQuantidadeDiasAulaPorSemana().get(diaSemanaDisponivel.getDiaSemanaEnum()) * 100;

                    if(primeiraVerificacao){
                        quantidadeDiasRestantesMelhorAproveitamento = (PORCENTAGEM_DIAS_AULA_OCUPADOS > 75) ? 100.0 : 0.0;
                        primeiraVerificacao = false;
                    }

                    final double QUANTIDADE_DIAS_AULA_NECESSARIOS_POR_DIA_SEMANA =
                            disciplinaDiasAula.quantidadeDiasAulaNecessariosPorDisciplina() / Math.ceil(disciplinaDiasAula.disciplinaQuantidadeDiasAulaPorSemana().get(diaSemanaDisponivel.getDiaSemanaEnum()));

                    final double QUANTIDADE_DIAS_AULA_RESTANTES =
                            quantidadeAulasPorDiaDaSemana.get(diaSemanaDisponivel.getDiaSemanaEnum()) - QUANTIDADE_DIAS_AULA_NECESSARIOS_POR_DIA_SEMANA;

                    if ((QUANTIDADE_DIAS_AULA_RESTANTES >= 0) &&
                            ((PORCENTAGEM_DIAS_AULA_OCUPADOS > 75) ? (quantidadeDiasRestantesMelhorAproveitamento > QUANTIDADE_DIAS_AULA_RESTANTES) : (quantidadeDiasRestantesMelhorAproveitamento < QUANTIDADE_DIAS_AULA_RESTANTES))
                    ) {

                        quantidadeDiasRestantesMelhorAproveitamento = QUANTIDADE_DIAS_AULA_RESTANTES;
                        CronogramaDisciplinaDom cronogramaDisciplina =
                                new CronogramaDisciplinaDom(disciplinaDiasAula.disciplina(), diaSemanaDisponivel.getDiaSemanaEnum(), QUANTIDADE_DIAS_AULA_RESTANTES);

                        final boolean DIA_DA_SEMANA_EM_USO = cronogramaFaseOficial.stream()
                                .anyMatch(cronogramaDisciplinaMath -> (cronogramaDisciplinaMath.getDiaSemanaEnum().equals(diaSemanaDisponivel.getDiaSemanaEnum())));

                        final double DISCIPLINA_QUANTIDADE_DIAS_AULA_RESTANTES = cronogramaFaseOficial.stream()
                                .filter(cronogramaDisciplinaMath -> (cronogramaDisciplinaMath.getDiaSemanaEnum().equals(diaSemanaDisponivel.getDiaSemanaEnum())))
                                .mapToDouble(CronogramaDisciplinaDom::getQuantidadeDiasAulaRestante)
                                .sum();

                        if(!DIA_DA_SEMANA_EM_USO || DISCIPLINA_QUANTIDADE_DIAS_AULA_RESTANTES >= QUANTIDADE_DIAS_AULA_NECESSARIOS_POR_DIA_SEMANA){
                                cronogramaFaseOficial.add(cronogramaDisciplina);
                                cronogramaFaseOficial.remove(cronogramaDisciplinaAntigo);

                                cronogramaDisciplinaAntigo = cronogramaDisciplina;
                        }
                    }
                }//fim for diasemana
            }//quantidadeDiasAulaPorSemana
        }//fim map

        for(CronogramaDisciplinaDom cronogramaDisciplina : cronogramaFaseOficial){
            System.out.println(cronogramaDisciplina.getDisciplina().getNome() + " "
                    + cronogramaDisciplina.getDisciplina().getCargaHoraria() + " "
                    + cronogramaDisciplina.getDiaSemanaEnum() + " "
                    + cronogramaDisciplina.getQuantidadeDiasAulaRestante());
            cronogramaDisciplina.getDisciplina().getProfessor().getDiasSemanaDisponivel()
                    .stream()
                    .forEach(diaSemanaDisponivel -> System.out.println(diaSemanaDisponivel.getDiaSemanaEnum()));
            System.out.println("\n");
        }

        return cronogramaFaseOficial;
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

    private Map<DiaSemanaEnum,Integer> buscarQuantidadeAulasDisponveisPorDiaDaSemana(LocalDate dataInicial, LocalDate dataFinal){
        Map<DiaSemanaEnum,Integer> quantidadeAulasPorDiaDaSemana = new HashMap<>();

        LocalDate dataInicialAuxiliar = dataInicial;
        while(dataInicialAuxiliar.isBefore(dataFinal)){
            DayOfWeek dayOfWeek = dataInicialAuxiliar.getDayOfWeek();
            if(!dayOfWeek.equals(DayOfWeek.SUNDAY)){
                DiaSemanaEnum diaDaSemana = DiaSemanaEnum.dayOfWeekParaDiaSemanaEnum(dayOfWeek);
                quantidadeAulasPorDiaDaSemana.merge(diaDaSemana, 1, Integer::sum);
            }
            dataInicialAuxiliar = dataInicialAuxiliar.plusDays(1);
        }

        return quantidadeAulasPorDiaDaSemana;
    }

    private Set<CronogramaDisciplinaDiasAulaDom> buscarQuantidadeAulasPorDisciplina(Long faseId, Map<DiaSemanaEnum,Integer> quantidadeAulasPorDiaDaSemana){
        Set<Disciplina> disciplinasEncontradas =
                disciplinaRepository.buscarDisciplinasPorFaseIdOrdenandoPorQtdDiaDisponivelProfessor(faseId).get();//faseId
        Set<CronogramaDisciplinaDiasAulaDom> disciplinasDiasAula = new LinkedHashSet<>();

        for (Disciplina disciplina : disciplinasEncontradas){
            Map<DiaSemanaEnum, Double> mapDisciplinaQuantidadeDiasAulaPorSemana = new LinkedHashMap<>();

            double quantidadeDiasAulaNecessariosPorDisciplina =
                    Math.floor(disciplina.getCargaHoraria() / disciplina.getCargaHorariaDiaria());

            for(Map.Entry<DiaSemanaEnum,Integer> entry : quantidadeAulasPorDiaDaSemana.entrySet()){
               double disciplinaQuantidadeDiasAulaPorSemana =
                       quantidadeDiasAulaNecessariosPorDisciplina / entry.getValue();

                mapDisciplinaQuantidadeDiasAulaPorSemana
                        .put(entry.getKey(),disciplinaQuantidadeDiasAulaPorSemana);
            }

            disciplinasDiasAula.add(
                    new CronogramaDisciplinaDiasAulaDom(
                            disciplina,
                            quantidadeDiasAulaNecessariosPorDisciplina,
                            mapDisciplinaQuantidadeDiasAulaPorSemana
                    ));
        }

        return disciplinasDiasAula;
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
