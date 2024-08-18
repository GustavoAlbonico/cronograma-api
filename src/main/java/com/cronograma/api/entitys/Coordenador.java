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
@SQLDelete(sql = "UPDATE coordenador SET status = 0 WHERE id=?")
@Getter
@Setter
public class Coordenador {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nomeCompleto;

    @Column(nullable = false, length = 11)
    private String cpf;

    @Column(nullable = false, length = 50)
    private String telefone;

    @Column(nullable = false,columnDefinition = "DATE DEFAULT CURRENT_DATE")
    private LocalDate dataCriacao;

    @Column(nullable = false,columnDefinition = "INTEGER DEFAULT 1")
    private Integer status;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @OneToOne
    @JoinColumn(nullable = false)
    private Usuario usuario;

    @OneToMany(mappedBy = "coordenador")
    private Set<Curso> cursos = new HashSet<>();
}
