package com.gs.energykids.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

@Entity
@Table(name = "DICA_ECONOMIA")
public class DicaEconomia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "DICA_ID")
    private Long id;

    @NotBlank(message = "A descrição da dica é obrigatória")
    @Column(name = "DESCRICAO", nullable = false, length = 255)
    private String descricao;

    @NotNull(message = "A data de criação é obrigatória")
    @Column(name = "DATA_CRIACAO", nullable = false)
    private LocalDate dataCriacao;

    public DicaEconomia() {
    }

    public DicaEconomia(String descricao, LocalDate dataCriacao) {
        this.descricao = descricao;
        this.dataCriacao = dataCriacao;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public LocalDate getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(LocalDate dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    @Override
    public String toString() {
        return "DicaEconomia{" +
                "id=" + id +
                ", descricao='" + descricao + '\'' +
                ", dataCriacao=" + dataCriacao +
                '}';
    }
}
