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
@SQLDelete(sql = "UPDATE disciplina SET status = 0 WHERE id=?")
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

    @Column(nullable = false,columnDefinition = "DATE DEFAULT CURRENT_DATE")
    private LocalDate dataCriacao;

    @Column(nullable = false,columnDefinition = "INTEGER DEFAULT 1")
    private Integer status;

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

    @Override
    public String toString() {
        return "Disciplina{" + '\'' +
                "id=" + id + '\'' +
                ", nome='" + nome + '\'' +
                ", cargaHoraria=" + cargaHoraria + '\'' +
                ", cargaHorariaDiaria=" + cargaHorariaDiaria + '\'' +
                ", corHexadecimal='" + corHexadecimal + '\'' +
                ", dataCriacao=" + dataCriacao + '\'' +
                ", status=" + status + '\'' +
                ", curso=" + curso + '\'' +
                ", fase=" + fase + '\'' +
                ", professor=" + professor + '\'' +
                ", diasCronograma=" + diasCronograma + '\'' +
                '}';
    }
}
