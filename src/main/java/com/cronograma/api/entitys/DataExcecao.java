package com.cronograma.api.entitys;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;

import java.time.LocalDate;

@Entity
@SQLDelete(sql = "UPDATE dia_excecao SET status = 0 WHERE id=?")
@Getter
@Setter
public class DataExcecao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String motivo;

    @Column(nullable = false)
    private LocalDate data;

    @Column(nullable = false,columnDefinition = "DATE DEFAULT CURRENT_DATE")
    private LocalDate dataCriacao;

    @Column(nullable = false,columnDefinition = "INTEGER DEFAULT 1")
    private Integer status;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @ManyToOne
    @JoinColumn(nullable = false)
    private Professor professor;
}
