package com.cronograma.api.useCases.cronograma;

import com.cronograma.api.entitys.DiaSemanaDisponivel;
import com.cronograma.api.entitys.Disciplina;
import com.cronograma.api.entitys.Professor;
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

        double quantidadeDiasDeAulasDisponiveisPorCronograma =
                buscarQuantidadeDiasDeAulasDisponiveisPorCronograma(cronograma.dataInicial(),cronograma.dataFinal());
        double quantidadeDiasDeAulaLetivosNecessarios =
                buscarQuantidadeDiasAulaLetivosNecessarios(1L);//FASEID
        int quantidadeDiasDeAulaLetivosPorSemana =
                buscarQuantidadeDeDiasLetivosPorSemana(quantidadeDiasDeAulasDisponiveisPorCronograma,quantidadeDiasDeAulaLetivosNecessarios);

        validarQuantidadeDiasDeAulaLetivosDisponiveisPelosProfessores(1L,1L,quantidadeDiasDeAulaLetivosPorSemana); // A TRATAR

        List<String> listaString = new ArrayList<>();
        return listaString;
    }

    public boolean validarQuantidadeDiasDeAulaLetivosDisponiveisPelosProfessores(Long cursoId, Long faseId,double quantidadeDiasDeAulaLetivosPorSemana) {
        Optional<Set<DiaSemanaEnum>> diasDaSemanaEncontrados =
                disciplinaRepository.buscarDiasDaSemanaDisponiveisRelacionadosProfessoresPorCursoIdPorFaseId(cursoId, faseId);
        Set<DiaSemanaEnum> diasDaSemanaDisponiveisPorProfessores = diasDaSemanaEncontrados.get();

        if (quantidadeDiasDeAulaLetivosPorSemana > diasDaSemanaDisponiveisPorProfessores.size()) {

            double quantidadeDiaDaSemanaAdicional = quantidadeDiasDeAulaLetivosPorSemana -  diasDaSemanaDisponiveisPorProfessores.size();

            Set<DiaSemanaEnum> diasDaSemanaSugestoes = Arrays.stream(DiaSemanaEnum.values())
                    .filter(diaSemanaEnum -> diaSemanaEnum != DiaSemanaEnum.DOMINGO)
                    .filter(diaSemanaEnum -> (!diasDaSemanaDisponiveisPorProfessores.contains(diaSemanaEnum)))
                    .collect(Collectors.toSet());
            //retornar a mensagem falando quantos dias da semana precisam
            // ser disponibilizado a mais e os dias possiveis a ser adicionado
            //e informando o curso e fase

            System.out.println(quantidadeDiaDaSemanaAdicional);

            for (DiaSemanaEnum dia :diasDaSemanaSugestoes){
                System.out.println(dia);
            }
        }
        return true;
    }

    private int buscarQuantidadeDeDiasLetivosPorSemana(double quantidadeDiasDeAulasDisponiveisPorCronograma, double quantidadeDiasDeAulaLetivosNecessarios){
        double mediaDiasDeAulaDisponiveisPorDiaDaSemana = quantidadeDiasDeAulasDisponiveisPorCronograma / (DiaSemanaEnum.values().length - 1); //tirando domingo

        int quantidadeDeDiasLetivosPorSemana = 0;
        double diasAulaLetivosNecessariosAuxiliar = 0.0;

        while (diasAulaLetivosNecessariosAuxiliar < quantidadeDiasDeAulaLetivosNecessarios){
            quantidadeDeDiasLetivosPorSemana++;
            diasAulaLetivosNecessariosAuxiliar+= mediaDiasDeAulaDisponiveisPorDiaDaSemana;
        }

        return quantidadeDeDiasLetivosPorSemana;
    }

    private double buscarQuantidadeDiasAulaLetivosNecessarios(Long faseId){
        Optional<Set<Disciplina>> disciplinasEncontradas = disciplinaRepository.findByFaseId(faseId);//faseId
        Set<Disciplina> listaDisciplinas = disciplinasEncontradas.get();

        return  listaDisciplinas.stream()
                .map(disciplina ->
                        (disciplina.getCargaHoraria() / disciplina.getCargaHorariaDiaria()))
                .mapToDouble(Double::doubleValue)
                .sum();
    }

    private double buscarQuantidadeDiasDeAulasDisponiveisPorCronograma(LocalDate cronogramaDataInicial,LocalDate cronogramaDataFinal){
        double quantidadeDiasDeAulasDisponiveisPorCronograma = 0.0;

        LocalDate dataInicialAuxiliar = cronogramaDataInicial;
        while(dataInicialAuxiliar.isBefore(cronogramaDataFinal)){
            DayOfWeek dayOfWeek = dataInicialAuxiliar.getDayOfWeek();
            if(!dayOfWeek.equals(DayOfWeek.SUNDAY)){
                quantidadeDiasDeAulasDisponiveisPorCronograma++;
            }
            dataInicialAuxiliar = dataInicialAuxiliar.plusDays(1);
        }

        return quantidadeDiasDeAulasDisponiveisPorCronograma;
    }
}
