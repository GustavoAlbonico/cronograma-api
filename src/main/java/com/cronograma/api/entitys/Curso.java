package com.cronograma.api.entitys;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@SQLDelete(sql = "UPDATE curso SET status = 0 WHERE id=?")
@Getter
@Setter
public class Curso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false,columnDefinition = "DATE DEFAULT CURRENT_DATE")
    private LocalDate dataCriacao;

    @Column(nullable = false,columnDefinition = "INTEGER DEFAULT 1")
    private Integer status;

    @ManyToMany
    @JoinTable(name = "curso_fase",joinColumns = @JoinColumn,inverseJoinColumns = @JoinColumn)
    private Set<Fase> fases = new HashSet<>();

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @ManyToOne
    @JoinColumn
    private Coordenador coordenador;

    @OneToMany(mappedBy = "curso")
    private Set<Cronograma> cronogramas =  new HashSet<>();

    @OneToMany(mappedBy = "curso")
    private Set<Disciplina> disciplinas = new HashSet<>();
}
