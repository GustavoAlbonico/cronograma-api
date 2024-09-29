package com.cronograma.api.entitys;

import com.cronograma.api.entitys.enums.NivelAcessoEnum;
import com.cronograma.api.entitys.enums.StatusEnum;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
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

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false, unique = true, length = 11)
    private String cpf;

    @Column(nullable = false)
    private String senha;

    @Column(nullable = false,columnDefinition = "VARCHAR(255) DEFAULT 'ATIVO'")
    @Enumerated(EnumType.STRING)
    private StatusEnum statusEnum;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "usuario_nivel_acesso",
            joinColumns = @JoinColumn(name = "usuario_id"),
            inverseJoinColumns = @JoinColumn(name = "nivel_acesso_id")
    )
    private Set<NivelAcesso> niveisAcesso = new HashSet<>();

    @OneToOne(mappedBy = "usuario")
    private Professor professor;

    @OneToOne(mappedBy = "usuario")
    private Coordenador coordenador;

    @OneToOne(mappedBy = "usuario")
    private Aluno aluno;

    @OneToMany(mappedBy = "usuario",fetch = FetchType.LAZY)
    private Set<DataBloqueada> datasBloqueadas = new HashSet<>();

    @OneToMany(mappedBy = "usuario",fetch = FetchType.LAZY)
    private Set<Evento> eventos =  new HashSet<>();

    @PrePersist
    void defaultStatusEnum(){
        statusEnum = StatusEnum.ATIVO;
    }

}
