package com.cronograma.api.entitys;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
public class NivelAcesso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String nome;

    private String descricao;

    @Column(nullable = false)
    private Integer rankingAcesso;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "nivel_acesso_controller",
            joinColumns = @JoinColumn(name = "nivel_acesso_id"),
            inverseJoinColumns = @JoinColumn(name = "controller_id")
    )
    private Set<Controller> controllers = new HashSet<>();

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @ManyToMany(mappedBy = "niveisAcesso", fetch = FetchType.LAZY)
    private Set<Usuario> usuarios =  new HashSet<>();

}
