package com.cronograma.api.useCases.cronograma.domains;
import java.time.LocalDate;

public record CronogramaRequestDom(
        LocalDate dataInicial,
        LocalDate dataFinal,
        Long cursoId) {
}
