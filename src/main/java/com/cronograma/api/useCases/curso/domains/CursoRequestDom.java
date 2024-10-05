package com.cronograma.api.useCases.curso.domains;

import com.cronograma.api.entitys.enums.StatusEnum;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CursoRequestDom {
    private String nome;
    private String sigla;
    private Long coordenadorId;
    private List<Long> faseIds;
}
