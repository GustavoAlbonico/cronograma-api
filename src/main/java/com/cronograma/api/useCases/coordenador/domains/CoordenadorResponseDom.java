package com.cronograma.api.useCases.coordenador.domains;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CoordenadorResponseDom {
    private Long id;
    private String cpf;
    private String nome;
    private String telefone;
    private String email;
}
