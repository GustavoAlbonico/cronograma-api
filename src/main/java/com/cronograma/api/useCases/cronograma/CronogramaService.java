package com.cronograma.api.useCases.cronograma;

import com.cronograma.api.entitys.DiaSemanaDisponivel;
import com.cronograma.api.entitys.Disciplina;
import com.cronograma.api.entitys.enums.DiaSemanaEnum;
import com.cronograma.api.useCases.cronograma.domains.CronogramaRequestDom;
import com.cronograma.api.useCases.cronograma.domains.CronogramaResponseDom;
import com.cronograma.api.useCases.disciplina.implement.repositorys.DisciplinaRepository;
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

    public List<String> gerarCronograma(CronogramaRequestDom cronograma){

        Set<DiaSemanaEnum> diasSemanaLetivos =
                buscarQuantidadeDeDiasLetivosPorSemana(cronograma.dataInicial(),cronograma.dataFinal(),1L);//FASEID

        List<String> listaString = new ArrayList<>();
        return listaString;
    }

    private Set<DiaSemanaEnum> buscarQuantidadeDeDiasLetivosPorSemana(LocalDate cronogramaDataInicial, LocalDate cronogramaDataFinal,Long faseId){

        Map<DiaSemanaEnum,Integer> diasAulaPorDiaDaSemana = new LinkedHashMap<>();

        Double diasAulasDisponiveisSegundaASexta = 0.0;
        Double diasAulasDisponiveisSegundaASabado = 0.0;

        LocalDate dataInicialAuxiliar = cronogramaDataInicial;
        while(dataInicialAuxiliar.isBefore(cronogramaDataFinal)){
            DayOfWeek dayOfWeek = dataInicialAuxiliar.getDayOfWeek();
            if(!dayOfWeek.equals(DayOfWeek.SUNDAY)){
                DiaSemanaEnum diaDaSemana = DiaSemanaEnum.dayOfWeekParaDiaSemanaEnum(dayOfWeek);
                diasAulaPorDiaDaSemana.merge(diaDaSemana, 1, Integer::sum);
                if(!dayOfWeek.equals(DayOfWeek.SATURDAY)){
                    diasAulasDisponiveisSegundaASexta++;
                }
                diasAulasDisponiveisSegundaASabado++;
            }
            dataInicialAuxiliar = dataInicialAuxiliar.plusDays(1);
        }

        for(Map.Entry<DiaSemanaEnum,Integer> entry : diasAulaPorDiaDaSemana.entrySet()){
            System.out.println(entry.getKey() + " " + entry.getValue());
        }

        System.out.println("Segunda a Sexta" +diasAulasDisponiveisSegundaASexta);
        System.out.println("Segunda a Sabado" + diasAulasDisponiveisSegundaASabado);

//        Optional<Set<Disciplina>> disciplinasEncontradas = disciplinaRepository.findByFaseId(faseId);//faseId
//        Set<Disciplina> listaDisciplinas = disciplinasEncontradas.get();
//
//        Map<Disciplina,Double> diasAulaPorDisciplina = listaDisciplinas.stream().collect(Collectors.toMap( //SE NAO USAR JA FAZER A SOMA AQUI
//                disciplina -> disciplina,
//                disciplina -> disciplina.getCargaHoraria() / disciplina.getCargaHorariaDiaria()
//        ));
//
//        Double diasAulaTotalDisciplinas =
//                diasAulaPorDisciplina.values().stream()
//                        .mapToDouble(Double::doubleValue)
//                        .sum();


        Set<DiaSemanaEnum> lista = new HashSet<>();
        return lista;
    }
}
