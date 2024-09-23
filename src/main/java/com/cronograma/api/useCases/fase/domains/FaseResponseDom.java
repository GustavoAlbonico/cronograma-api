package com.cronograma.api.useCases.fase.domains;

import com.cronograma.api.entitys.enums.StatusEnum;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FaseResponseDom {
    private Long id;
    private Integer numero;
    private StatusEnum statusEnum;
}
