package com.cronograma.api.useCases.coordenador.domains;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CoordenadorRequestDom {
    private String cpf;
    private String nome;
    private String telefone;
    private String email;
}
