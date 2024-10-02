package com.cronograma.api.useCases.usuario.domains;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UsuarioRequestDom {
   private String cpf;
   private String nome;
   private String email;
}
