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
public class Controller {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String nome;

    @ManyToMany
    @JoinTable(
            name = "controller_funcionalidade",
            joinColumns = @JoinColumn(name = "controller_id"),
            inverseJoinColumns = @JoinColumn(name = "funcionalidade_id")
    )
    private Set<Funcionalidade> funcionalidades = new HashSet<>();

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @ManyToMany(mappedBy = "controllers", fetch = FetchType.LAZY)
    private Set<NivelAcesso> niveisAcesso =  new HashSet<>();
}
