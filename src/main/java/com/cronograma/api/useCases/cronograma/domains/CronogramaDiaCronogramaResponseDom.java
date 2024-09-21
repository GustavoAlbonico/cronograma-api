package com.cronograma.api.useCases.cronograma.domains;
import com.cronograma.api.entitys.enums.DataStatusEnum;
import com.cronograma.api.entitys.enums.DiaSemanaEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CronogramaDiaCronogramaResponseDom {
    private Long id;
    private LocalDate data;
    private DiaSemanaEnum diaSemanaEnum;
    private DataStatusEnum dataStatusEnum;
    private String professorNome;
    private String disciplinaNome;
    private String corHexadecimal;
}
