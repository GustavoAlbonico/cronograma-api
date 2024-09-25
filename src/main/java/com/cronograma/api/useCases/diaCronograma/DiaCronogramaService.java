package com.cronograma.api.useCases.diaCronograma;

import com.cronograma.api.entitys.*;
import com.cronograma.api.entitys.enums.DataStatusEnum;
import com.cronograma.api.entitys.enums.DiaSemanaEnum;
import com.cronograma.api.exceptions.DiaCronogramaException;
import com.cronograma.api.useCases.cronograma.domains.CronogramaDisciplinaDom;
import com.cronograma.api.useCases.diaCronograma.implement.mappers.DiaCronogramaMapper;
import com.cronograma.api.useCases.diaCronograma.implement.repositorys.DiaCronogramaRepository;
import com.cronograma.api.useCases.fase.implement.repositorys.FaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;


@Service
public class DiaCronogramaService {

    @Autowired
    private DiaCronogramaRepository diaCronogramaRepository;

    @Autowired
    private FaseRepository faseRepository;

    @Autowired
    private DiaCronogramaMapper diaCronogramaMapper;

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

    public void editarDiaCronograma(Long primeiroDiaCronogramaId, Long segundoDiaCronogramaId){

        //validação de usario preciso pegar atraves do jwt

        if(primeiroDiaCronogramaId.equals(segundoDiaCronogramaId)){
            throw new DiaCronogramaException("Datas selecionadas não podem ser iguais!");
        }

        DiaCronograma  primeiroDiaCronograma = diaCronogramaRepository.findById(primeiroDiaCronogramaId)
                .orElseThrow(() -> new DiaCronogramaException("Nenhuma data encontrada!"));
        DiaCronograma  segundoDiaCronograma = diaCronogramaRepository.findById(segundoDiaCronogramaId)
                .orElseThrow(() -> new DiaCronogramaException("Nenhuma data encontrada!"));

        if (
            primeiroDiaCronograma.getDataStatusEnum().equals(DataStatusEnum.BLOQUEADA) ||
            segundoDiaCronograma.getDataStatusEnum().equals(DataStatusEnum.BLOQUEADA)
        ) {
            throw new DiaCronogramaException("Data(s) selecionada(s) inválida(s)!");
        }

        if(
           primeiroDiaCronograma.getDataStatusEnum().equals(DataStatusEnum.DISPONIVEL) &&
           segundoDiaCronograma.getDataStatusEnum().equals(DataStatusEnum.DISPONIVEL)
        ){
            throw new DiaCronogramaException("Datas selecionadas invalidas!");
        }

        if(
           !primeiroDiaCronograma.getCronograma().getCurso().getId().equals(segundoDiaCronograma.getCronograma().getCurso().getId()) ||
           !primeiroDiaCronograma.getFase().getId().equals(segundoDiaCronograma.getFase().getId())
        ){
            throw new DiaCronogramaException("Uma das datas selecionadas não pertence ao mesmo curso ou fase!");
        }

        if(primeiroDiaCronograma.getDisciplina() != null && segundoDiaCronograma.getDisciplina() != null){
            if (primeiroDiaCronograma.getDisciplina().getId().equals(segundoDiaCronograma.getDisciplina().getId())){
                throw new DiaCronogramaException("As datas selecionadas pertencem a mesma disciplina!");
            }
        }

        validarPossuiConflitoData(primeiroDiaCronograma,segundoDiaCronograma);//TESTAR
        validarPossuiDiaSemanaDisponivel(primeiroDiaCronograma,segundoDiaCronograma);//TESTAR

        validarPossuiConflitoData(segundoDiaCronograma,primeiroDiaCronograma);//TESTAR
        validarPossuiDiaSemanaDisponivel(segundoDiaCronograma,primeiroDiaCronograma);

        final DiaCronograma primeiroDiaCronogramaEditado = new DiaCronograma();
        primeiroDiaCronogramaEditado.setDisciplina(primeiroDiaCronograma.getDisciplina());
        primeiroDiaCronogramaEditado.setDataStatusEnum(primeiroDiaCronograma.getDataStatusEnum());
        diaCronogramaMapper.diaCronogramaParaDiaCronogramaEditado(segundoDiaCronograma,primeiroDiaCronogramaEditado);

        final DiaCronograma segundoDiaCronogramaEditado = new DiaCronograma();
        segundoDiaCronogramaEditado.setDisciplina(segundoDiaCronograma.getDisciplina());
        segundoDiaCronogramaEditado.setDataStatusEnum(segundoDiaCronograma.getDataStatusEnum());
        diaCronogramaMapper.diaCronogramaParaDiaCronogramaEditado(primeiroDiaCronograma,segundoDiaCronogramaEditado);

        diaCronogramaRepository.save(primeiroDiaCronogramaEditado);
        diaCronogramaRepository.save(segundoDiaCronogramaEditado);
    }

    private void validarPossuiConflitoData(DiaCronograma  diaCronogramaEdicao, DiaCronograma  diaCronogramaAtual){
        if(diaCronogramaEdicao.getDataStatusEnum().equals(DataStatusEnum.OCUPADA) && diaCronogramaEdicao.getDisciplina().getProfessor() != null){
            Optional<DiaCronograma> diaCronogramaEcontrado = diaCronogramaRepository.buscarPorProfessorIdPorData(
                    diaCronogramaEdicao.getDisciplina().getProfessor().getId(),
                    diaCronogramaAtual.getData());

            if(diaCronogramaEcontrado.isPresent()){
                if(
                   !diaCronogramaEcontrado.get().getCronograma().getCurso().getId().equals(diaCronogramaEdicao.getCronograma().getCurso().getId()) ||
                   !diaCronogramaEcontrado.get().getFase().getId().equals(diaCronogramaEdicao.getFase().getId())
                ){
                    DateTimeFormatter dataFormatoPtBr = DateTimeFormatter.ofPattern("dd/MM/yyyy");

                    throw new DiaCronogramaException(
                            diaCronogramaEdicao.getDisciplina().getProfessor().getUsuario().getNome()  +
                                    " já leciona na data (" + dataFormatoPtBr.format(diaCronogramaAtual.getData()) + ") em outra fase ou curso!");
                }
            }
        }
    }

    private void validarPossuiDiaSemanaDisponivel (DiaCronograma  diaCronogramaEdicao, DiaCronograma  diaCronogramaAtual){
        if(diaCronogramaEdicao.getDataStatusEnum().equals(DataStatusEnum.OCUPADA) && diaCronogramaEdicao.getDisciplina().getProfessor() != null) {
            final boolean professorPossuiDiaSemanaDisponivel = diaCronogramaEdicao.getDisciplina().getProfessor().getDiasSemanaDisponivel().stream()
                    .anyMatch(diaSemanaDisponivel -> diaSemanaDisponivel.getDiaSemanaEnum().equals(diaCronogramaAtual.getDiaSemanaEnum()));

            if (!professorPossuiDiaSemanaDisponivel) {
                throw new DiaCronogramaException(
                        diaCronogramaEdicao.getDisciplina().getProfessor().getUsuario().getNome() +
                                " não possui " + diaCronogramaAtual.getDiaSemanaEnum() + " como dia da semana disponivel!");
            }
        }
    }
}
