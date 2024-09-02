package com.cronograma.api.useCases.cronograma.domains;
import com.cronograma.api.entitys.enums.DiaSemanaEnum;
import java.util.List;

public record TesteResponseCronogramaDom( double faseId,
                                        String disciplinaNome,
                                         double disicplinaCargaHoraria,
                                         int ordemPrioridadePorDiaSemana,
                                         double quantidadeDiasAula,
                                         DiaSemanaEnum diaSemanaEnum,
                                         String nomeProfessor,
                                         List<DiaSemanaEnum> listaDiasSemanaDisponiveis) {
}
