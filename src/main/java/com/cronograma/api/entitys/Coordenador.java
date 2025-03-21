package com.cronograma.api.entitys;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
public class Coordenador {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String telefone;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @OneToOne
    @JoinColumn(nullable = false)
    private Usuario usuario;

    @OneToMany(mappedBy = "coordenador",fetch = FetchType.EAGER)
    private Set<Curso> cursos = new HashSet<>();


}
