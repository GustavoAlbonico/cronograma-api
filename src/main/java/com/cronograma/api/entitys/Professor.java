package com.cronograma.api.entitys;

import com.cronograma.api.entitys.enums.StatusEnum;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDate;
import java.util.*;

@Entity
@Getter
@Setter
public class Professor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String telefone;

    @Column(nullable = false,columnDefinition = "VARCHAR(255) DEFAULT 'ATIVO'")
    @Enumerated(EnumType.STRING)
    private StatusEnum statusEnum;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @OneToOne
    @JoinColumn(nullable = false)
    private Usuario usuario;

    @OneToMany(mappedBy = "professor")
    private Set<DiaSemanaDisponivel> diasSemanaDisponivel =  new HashSet<>();

    @OneToMany(mappedBy = "professor",fetch = FetchType.EAGER)
    private Set<Disciplina> disciplinas = new HashSet<>();

    @PrePersist
    void defaultStatusEnum(){
        statusEnum = StatusEnum.ATIVO;
    }

}
