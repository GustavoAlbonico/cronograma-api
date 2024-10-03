package com.cronograma.api.entitys;

import com.cronograma.api.entitys.enums.DiaSemanaEnum;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
public class DiaSemanaDisponivel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    @Enumerated(EnumType.STRING)
    private DiaSemanaEnum diaSemanaEnum;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @ManyToMany(mappedBy = "diasSemanaDisponivel" , fetch = FetchType.LAZY)
    private Set<Professor> professores = new HashSet<>();
}
