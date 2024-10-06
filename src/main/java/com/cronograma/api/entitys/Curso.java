package com.cronograma.api.entitys;

import com.cronograma.api.entitys.enums.StatusEnum;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
public class Curso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false, length = 6)
    private String sigla;

    @Column(nullable = false,columnDefinition = "VARCHAR(255) DEFAULT 'ATIVO'")
    @Enumerated(EnumType.STRING)
    private StatusEnum statusEnum;

    @ManyToMany
    @JoinTable(
            name = "curso_fase",
            joinColumns = @JoinColumn(name = "curso_id"),
            inverseJoinColumns = @JoinColumn(name = "fase_id")
    )
    private Set<Fase> fases = new HashSet<>();

    @OneToMany(mappedBy = "curso")
    private Set<Aluno> alunos =  new HashSet<>();

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @ManyToOne
    @JoinColumn
    private Coordenador coordenador;

    @OneToMany(mappedBy = "curso")
    private Set<Cronograma> cronogramas =  new HashSet<>();

    @OneToMany(mappedBy = "curso")
    private Set<Disciplina> disciplinas = new HashSet<>();

    @OneToMany(mappedBy = "curso")
    private Set<Evento> eventos =  new HashSet<>();

    @PrePersist
    void defaultStatusEnum(){
        statusEnum = StatusEnum.ATIVO;
    }
}
