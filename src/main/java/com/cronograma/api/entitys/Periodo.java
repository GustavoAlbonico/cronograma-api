package com.cronograma.api.entitys;

import com.cronograma.api.entitys.enums.StatusEnum;
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
public class Periodo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false)
    private LocalDate dataInicial;

    @Column(nullable = false)
    private LocalDate dataFinal;

    @Column(nullable = false,columnDefinition = "VARCHAR(255) DEFAULT 'ATIVO'")
    @Enumerated(EnumType.STRING)
    private StatusEnum statusEnum;

    @OneToMany(mappedBy = "periodo")
    private Set<Cronograma> cronogramas = new HashSet<>();

    @PrePersist
    void defaultStatusEnum(){
        statusEnum = StatusEnum.ATIVO;
    }
}
