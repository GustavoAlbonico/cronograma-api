package com.cronograma.api.useCases.cronograma.domains;

import com.cronograma.api.entitys.DiaSemanaDisponivel;
import com.cronograma.api.entitys.enums.DiaSemanaEnum;

import java.util.List;
import java.util.Set;

public record TesteResponseCronogramaDom(String disciplinaNome,
                                         double disicplinaCargaHoraria,
                                         int ordemPrioridadePorDiaSemana,
                                         DiaSemanaEnum diaSemanaEnum,
                                         String nomeProfessor,
                                         List<DiaSemanaEnum> listaDiasSemanaDisponiveis) {
}
