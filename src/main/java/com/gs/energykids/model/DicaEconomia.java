package com.gs.energykids.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "DICAECONOMIA")
public class DicaEconomia {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "dica_economia_seq")
    @SequenceGenerator(name = "dica_economia_seq", sequenceName = "SEQ_DICA_ECONOMIA", allocationSize = 1)
    @Column(name = "DICA_ID")
    private Long id;

    @NotBlank(message = "A descrição da dica é obrigatória")
    @Column(name = "TEXTO_DICA", nullable = false, columnDefinition = "CLOB")
    private String descricao;

    @Column(name = "CATEGORIA", length = 50)
    private String categoria;

    @Column(name = "RELEVANCIA", length = 20)
    private String relevancia;

    public DicaEconomia() {
    }

    public DicaEconomia(String descricao, String categoria, String relevancia) {
        this.descricao = descricao;
        this.categoria = categoria;
        this.relevancia = relevancia;
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

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getRelevancia() {
        return relevancia;
    }

    public void setRelevancia(String relevancia) {
        this.relevancia = relevancia;
    }

    @Override
    public String toString() {
        return "DicaEconomia{" +
                "id=" + id +
                ", descricao='" + descricao + '\'' +
                ", categoria='" + categoria + '\'' +
                ", relevancia='" + relevancia + '\'' +
                '}';
    }
}
