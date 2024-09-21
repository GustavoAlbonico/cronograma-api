package com.cronograma.api.useCases.diaCronograma;

import com.cronograma.api.entitys.*;
import com.cronograma.api.entitys.enums.DataStatusEnum;
import com.cronograma.api.entitys.enums.DiaSemanaEnum;
import com.cronograma.api.exceptions.DiaCronogramaException;
import com.cronograma.api.useCases.cronograma.domains.CronogramaDisciplinaDom;
import com.cronograma.api.useCases.diaCronograma.implement.repositorys.DiaCronogramaRepository;
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
    private DiaCronogramaRepository diaCronogramaRepository;

    @Autowired
    private FaseRepository faseRepository;

    public void criarDiaCronograma(List<CronogramaDisciplinaDom> cronogramaDisciplinasPorCurso,
                                   Cronograma cronogramaSalvo,
                                   Periodo periodo,
                                   List<DataBloqueada> datasBloqueadas,
                                   List<DiaCronograma> diasCronogramaReferenteProfessoresCursoAtual
    ){

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
                DayOfWeek dayOfWeek = dataInicialAuxiliar.getDayOfWeek();

                if (!dayOfWeek.equals(DayOfWeek.SUNDAY)) {
                    boolean dataDisponivel = true;
                    final DiaSemanaEnum diaDaSemana = DiaSemanaEnum.dayOfWeekParaDiaSemanaEnum(dayOfWeek);

                    DiaCronograma diaCronograma = new DiaCronograma();
                    diaCronograma.setData(dataInicialAuxiliar);
                    diaCronograma.setDiaSemanaEnum(diaDaSemana);
                    diaCronograma.setFase(entry.getKey());
                    diaCronograma.setCronograma(cronogramaSalvo);

                    LocalDate dataInicialAuxiliarLocal = dataInicialAuxiliar;
                    if(datasBloqueadas.stream().anyMatch(dataBloqueada -> dataBloqueada.getData().isEqual(dataInicialAuxiliarLocal))){
                        diaCronograma.setDataStatusEnum(DataStatusEnum.BLOQUEADA);
                        dataDisponivel = false;
                    } else {
                        for (CronogramaDisciplinaDom cronogramaDisciplina : entry.getValue().get(diaDaSemana)){
                            double quantidadeDiasAula = cronogramaDisciplina.getQuantidadeDiasAula();

                            if (quantidadeDiasAula > 0){
                                List<LocalDate> datasOcupadasProfessorAtual = diasCronograma.stream()
                                        .filter(diaCronogramaAuxiliar ->
                                                    diaCronogramaAuxiliar.getDisciplina() != null &&
                                                    diaCronogramaAuxiliar.getDisciplina().getProfessor().getId()
                                                        .equals(cronogramaDisciplina.getDisciplina().getProfessor().getId()))
                                        .map(DiaCronograma::getData)
                                        .toList();

                                List<LocalDate> datasOcupadasProfessorAtualEmOutroCurso = diasCronogramaReferenteProfessoresCursoAtual.stream()
                                        .filter(diaCronogramaAuxiliar ->
                                                diaCronogramaAuxiliar.getDisciplina() != null &&
                                                        diaCronogramaAuxiliar.getDisciplina().getProfessor().getId()
                                                                .equals(cronogramaDisciplina.getDisciplina().getProfessor().getId()))
                                        .map(DiaCronograma::getData)
                                        .toList();

                                if(!datasOcupadasProfessorAtual.contains(dataInicialAuxiliar) && !datasOcupadasProfessorAtualEmOutroCurso.contains(dataInicialAuxiliar)){
                                    diaCronograma.setDataStatusEnum(DataStatusEnum.OCUPADA);
                                    diaCronograma.setDisciplina(cronogramaDisciplina.getDisciplina());
                                    cronogramaDisciplina.setQuantidadeDiasAula(quantidadeDiasAula - 1);
                                    dataDisponivel = false;
                                    break;
                                }
                            }
                        }
                    }
                    if (dataDisponivel){
                        diaCronograma.setDataStatusEnum(DataStatusEnum.DISPONIVEL);
                    }
                    diasCronograma.add(diaCronograma);
                }
                dataInicialAuxiliar = dataInicialAuxiliar.plusDays(1);
            }
        }
        diaCronogramaRepository.saveAll(diasCronograma);
    }
}
