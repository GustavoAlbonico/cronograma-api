package com.cronograma.api.entitys;

import com.cronograma.api.entitys.enums.BooleanEnum;
import com.cronograma.api.entitys.enums.StatusEnum;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
public class Disciplina {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false)
    private Double cargaHoraria;

    @Column(nullable = false)
    private Double cargaHorariaDiaria;

    @Column(nullable = false, length = 10)
    private String corHexadecimal;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private BooleanEnum extensaoBooleanEnum;

    @Column(nullable = false,columnDefinition = "VARCHAR(255) DEFAULT 'ATIVO'")
    @Enumerated(EnumType.STRING)
    private StatusEnum statusEnum;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @ManyToOne
    @JoinColumn(nullable = false)
    private Curso curso;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @ManyToOne
    @JoinColumn(nullable = false)
    private Fase fase;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @ManyToOne
    @JoinColumn
    private Professor professor;

    @OneToMany(mappedBy = "disciplina")
    private Set<DiaCronograma> diasCronograma = new HashSet<>();

    @PrePersist
    void defaultStatusEnum(){
        statusEnum = StatusEnum.ATIVO;
    }
}
