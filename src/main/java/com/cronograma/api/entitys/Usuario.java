package com.cronograma.api.entitys;

import com.cronograma.api.entitys.enums.NivelAcessoEnum;
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
@SQLDelete(sql = "UPDATE usuario SET status_enum = 'INATIVO' WHERE id=?")
@SQLRestriction("status_enum = 'ATIVO'")
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

    @Column(nullable = false,columnDefinition = "VARCHAR(255) DEFAULT 'ATIVO'")
    @Enumerated(EnumType.STRING)
    private StatusEnum statusEnum;

    @OneToOne(mappedBy = "usuario")
    private Professor professor;

    @OneToOne(mappedBy = "usuario")
    private Coordenador coordenador;

    @OneToMany(mappedBy = "usuario")
    private Set<DataBloqueada> datasBloqueadas = new HashSet<>();

    @OneToMany(mappedBy = "usuario")
    private Set<Evento> eventos =  new HashSet<>();

}
