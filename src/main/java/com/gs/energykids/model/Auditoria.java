package com.gs.energykids.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Table(name = "AUDITORIA")
public class Auditoria {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "auditoria_seq")
    @SequenceGenerator(name = "auditoria_seq", sequenceName = "SEQ_AUDITORIA", allocationSize = 1)
    @Column(name = "AUDIT_ID")
    private Long id;

    @NotBlank(message = "O nome da tabela alterada é obrigatório")
    @Column(name = "TABELA", nullable = false, length = 100)
    private String tabelaAlterada;

    @NotBlank(message = "A operação realizada é obrigatória")
    @Column(name = "OPERACAO", nullable = false, length = 10)
    private String operacao;

    @NotNull(message = "A data e hora da operação são obrigatórias")
    @Column(name = "DATA", nullable = false)
    private LocalDateTime dataHora;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "USUARIO_ID")
    private Usuario usuario;

    @Lob
    @Column(name = "DESCRICAO")
    private String detalhes;

    public Auditoria() {
    }

    public Auditoria(String tabelaAlterada, String operacao, LocalDateTime dataHora, Usuario usuario, String detalhes) {
        this.tabelaAlterada = tabelaAlterada;
        this.operacao = operacao;
        this.dataHora = dataHora;
        this.usuario = usuario;
        this.detalhes = detalhes;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTabelaAlterada() {
        return tabelaAlterada;
    }

    public void setTabelaAlterada(String tabelaAlterada) {
        this.tabelaAlterada = tabelaAlterada;
    }

    public String getOperacao() {
        return operacao;
    }

    public void setOperacao(String operacao) {
        this.operacao = operacao;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public void setDataHora(LocalDateTime dataHora) {
        this.dataHora = dataHora;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public String getDetalhes() {
        return detalhes;
    }

    public void setDetalhes(String detalhes) {
        this.detalhes = detalhes;
    }

    @Override
    public String toString() {
        return "Auditoria{" +
                "id=" + id +
                ", tabelaAlterada='" + tabelaAlterada + '\'' +
                ", operacao='" + operacao + '\'' +
                ", dataHora=" + dataHora +
                ", usuario=" + usuario +
                ", detalhes='" + detalhes + '\'' +
                '}';
    }
}
