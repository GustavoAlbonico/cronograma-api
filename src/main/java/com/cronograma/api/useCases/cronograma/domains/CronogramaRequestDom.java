package com.cronograma.api.useCases.cronograma.domains;
import java.time.LocalDate;

public record CronogramaRequestDom(
        Long periodoId,
        Long cursoId) {
}