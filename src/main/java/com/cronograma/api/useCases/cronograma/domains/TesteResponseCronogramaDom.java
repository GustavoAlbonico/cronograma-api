package com.cronograma.api.useCases.cronograma.domains;
import com.cronograma.api.entitys.enums.DiaSemanaEnum;
import java.util.List;

public record TesteResponseCronogramaDom( double faseId,
                                        String disciplinaNome,
                                         double disicplinaCargaHoraria,
                                         Integer ordemPrioridadePorDiaSemana,
                                         double quantidadeDiasAula,
                                         DiaSemanaEnum diaSemanaEnum,
                                         String nomeProfessor) {
}
