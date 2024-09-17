package com.cronograma.api.useCases.diaCronograma;

import com.cronograma.api.entitys.*;
import com.cronograma.api.entitys.enums.DiaSemanaEnum;
import com.cronograma.api.exceptions.DiaCronogramaException;
import com.cronograma.api.useCases.cronograma.CronogramaService;
import com.cronograma.api.useCases.cronograma.domains.CronogramaDisciplinaDom;
import com.cronograma.api.useCases.fase.implement.repositorys.FaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;


@Service
public class DiaCronogramaService {

    @Autowired
    private FaseRepository faseRepository;


    public void criarDiaCronograma(List<CronogramaDisciplinaDom> cronogramaDisciplinasPorCurso, Cronograma cronogramaSalvo, Periodo periodo, List<DataBloqueada> datasBloqueadas){

        Map<Long,Map<DiaSemanaEnum,List<CronogramaDisciplinaDom>>> cronogramaDisciplinasPorFaseIdDiaSemana = cronogramaDisciplinasPorCurso.stream()
                .collect(Collectors.groupingBy(
                            CronogramaDisciplinaDom::getFaseId,
                            LinkedHashMap::new,
                            Collectors.groupingBy(
                                    CronogramaDisciplinaDom::getDiaSemanaEnum,
                                    LinkedHashMap::new,
                                    Collectors.toList()
                            )
                        )
                );

        Map<Fase,Map<DiaSemanaEnum,List<CronogramaDisciplinaDom>>> cronogramaDisciplinasPorFaseDiaSemana =
                cronogramaDisciplinasPorFaseIdDiaSemana.entrySet().stream()
                        .collect(Collectors.toMap(
                                entry -> faseRepository.findById(entry.getKey()).orElseThrow(() -> new DiaCronogramaException("Nenhuma fase encontrada!")),
                                Map.Entry::getValue,
                                (a,b) -> a,
                                LinkedHashMap::new
                        ));


        List<DiaCronograma> diasCronograma = new ArrayList<>();

        for (Map.Entry<Fase,Map<DiaSemanaEnum,List<CronogramaDisciplinaDom>>> entry : cronogramaDisciplinasPorFaseDiaSemana.entrySet()) {

            LocalDate dataInicialAuxiliar = periodo.getDataInicial();
            while (dataInicialAuxiliar.isBefore(periodo.getDataFinal().plusDays(1L))) {
                LocalDate dataInicialAuxiliarLocal = dataInicialAuxiliar;

                DayOfWeek dayOfWeek = dataInicialAuxiliar.getDayOfWeek();
                if (!dayOfWeek.equals(DayOfWeek.SUNDAY)) {
                    DiaSemanaEnum diaDaSemana = DiaSemanaEnum.dayOfWeekParaDiaSemanaEnum(dayOfWeek);

                    if(datasBloqueadas.stream().anyMatch(dataBloqueada -> dataBloqueada.getData().isEqual(dataInicialAuxiliarLocal))){
                        DiaCronograma diaCronograma =  new DiaCronograma(
                                null,
                                dataInicialAuxiliar,
                                diaDaSemana,
                                entry.getKey(),
                                cronogramaSalvo,
                                null);

                        diasCronograma.add(diaCronograma);
                    } else {
                        for (CronogramaDisciplinaDom cronogramaDisciplina : entry.getValue().get(diaDaSemana)){
                            double quantidadeDiasAula = cronogramaDisciplina.getQuantidadeDiasAula();
                            if (quantidadeDiasAula > 0){

                                DiaCronograma diaCronograma =  new DiaCronograma(
                                        null,
                                        dataInicialAuxiliar,
                                        diaDaSemana,
                                        entry.getKey(),
                                        cronogramaSalvo,
                                        cronogramaDisciplina.getDisciplina());

                                diasCronograma.add(diaCronograma);
                                cronogramaDisciplina.setQuantidadeDiasAula(quantidadeDiasAula - 1);
                                break;
                            }
                        }
                    }
                }
                dataInicialAuxiliar = dataInicialAuxiliar.plusDays(1);
            }
        }


        Map<Fase,Map<DiaSemanaEnum,List<DiaCronograma>>> diaCronogramaPorFaseDiaSemana =
                diasCronograma.stream()
                        .collect(Collectors.groupingBy(
                                DiaCronograma::getFase,
                                LinkedHashMap::new,
                                Collectors.groupingBy(
                                        DiaCronograma::getDiaSemanaEnum,
                                        LinkedHashMap::new,
                                        Collectors.toList())
                                )
                        );



        //verificar os dias vazios para adicionar tambem

    }
}
