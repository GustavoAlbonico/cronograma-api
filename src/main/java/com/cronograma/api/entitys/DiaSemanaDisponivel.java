package com.cronograma.api.entitys;

import com.cronograma.api.entitys.enums.DiaSemanaEnum;
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

@Entity
@SQLDelete(sql = "UPDATE usuario SET status_enum = 'INATIVO' WHERE id=?")
@SQLRestriction("status_enum = 'ATIVO'")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DiaSemanaDisponivel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    DiaSemanaEnum diaSemanaEnum;

    @Column(nullable = false,columnDefinition = "VARCHAR(255) DEFAULT 'ATIVO'")
    @Enumerated(EnumType.STRING)
    private StatusEnum statusEnum;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @ManyToOne
    @JoinColumn(nullable = false)
    private Professor professor;

    @Override
    public String toString() {
        return "DiaSemanaDisponivel{" +
                "id=" + id +
                ", diaSemanaEnum=" + diaSemanaEnum +
                ", statusEnum=" + statusEnum +
                '}';
    }
}
