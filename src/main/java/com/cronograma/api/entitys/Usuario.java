package com.cronograma.api.entitys;

import com.cronograma.api.entitys.enums.NivelAcessoEnum;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;

import java.time.LocalDate;
@Entity
@SQLDelete(sql = "UPDATE usuario SET status = 0 WHERE id=?")
@Getter
@Setter
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String senha;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private NivelAcessoEnum nivelAcessoEnum;

    @Column(nullable = false,columnDefinition = "DATE DEFAULT CURRENT_DATE")
    private LocalDate dataCriacao;

    @Column(nullable = false,columnDefinition = "INTEGER DEFAULT 1")
    private Integer status;

    @OneToOne(mappedBy = "usuario")
    private Professor professor;

    @OneToOne(mappedBy = "usuario")
    private Coordenador coordenador;

}
