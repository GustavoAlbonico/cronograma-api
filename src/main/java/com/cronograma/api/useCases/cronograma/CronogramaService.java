package com.cronograma.api.useCases.cronograma;

import com.cronograma.api.entitys.*;
import com.cronograma.api.entitys.enums.DiaSemanaEnum;
import com.cronograma.api.entitys.enums.StatusEnum;
import com.cronograma.api.useCases.cronograma.domains.CronogramaRequestDom;
import com.cronograma.api.useCases.cronograma.domains.Teste;
import com.cronograma.api.useCases.curso.implement.repositorys.CursoRepository;
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


    public List<String> gerarCronograma(CronogramaRequestDom cronograma){

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

        Map<Disciplina,Double> quantidadeAulasPorDisciplina =
                buscarQuantidadeAulasNecessariosPorDisciplina(2L);

        Map<Disciplina,DiaSemanaEnum> cronogramaFaseOficial = new HashMap<>();//NAO SEI

        for (Map.Entry<Disciplina,Double> entry : quantidadeAulasPorDisciplina.entrySet()){
            double quantidadeDiasRestantesMelhorAproveitamento = 0.0;
            //A QUE FOR DO TIPO EXTENSÃO É SABADO

            for (DiaSemanaDisponivel diaSemanaDisponivel: entry.getKey().getProfessor().getDiasSemanaDisponivel()){
               double quantidadeAulaDiaDaSemana = quantidadeAulasPorDiaDaSemana.entrySet()
                        .stream()
                        .filter(quantidadeAulaMap -> quantidadeAulaMap.getKey() == diaSemanaDisponivel.getDiaSemanaEnum())
                        .mapToDouble(Map.Entry::getValue)
                        .sum();

               double quantidadeDiasRestantes = quantidadeAulaDiaDaSemana - entry.getValue();

               if(quantidadeDiasRestantes >= 0 && quantidadeDiasRestantesMelhorAproveitamento > quantidadeDiasRestantes){
                   quantidadeDiasRestantesMelhorAproveitamento = quantidadeDiasRestantes;
                   Disciplina chaveAntiga = entry.getKey();

                   cronogramaFaseOficial.remove(chaveAntiga);

                   cronogramaFaseOficial.put(entry.getKey(),diaSemanaDisponivel.getDiaSemanaEnum());
               }

            }//fim for diasemana
        }//fim map




//
//        for (Professor professor :professoresEncontrados){
//            for (DiaSemanaDisponivel diaSemanaDisponivel : professor.getDiasSemanaDisponivel()){
//
//                quantidadeAulasPorDisciplina.keySet().stream().filter(disciplina -> disciplina.getId())
//            }
//        }

        List<String> listaString = new ArrayList<>();
        return listaString;
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
        Map<DiaSemanaEnum,Integer> quantidadeAulasPorDiaDaSemana = new LinkedHashMap<>();

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

    private Map<Disciplina,Double> buscarQuantidadeAulasNecessariosPorDisciplina(Long faseId){
        Set<Disciplina> disciplinasEncontradas = disciplinaRepository.findByFaseId(faseId).get();//faseId
        Map<Disciplina,Double> quantidadeAulasPorDisiplina = new HashMap<>();

        for (Disciplina disciplina : disciplinasEncontradas){
            quantidadeAulasPorDisiplina
                    .put(disciplina,disciplina.getCargaHoraria() / disciplina.getCargaHorariaDiaria());
        }

        return quantidadeAulasPorDisiplina;
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
