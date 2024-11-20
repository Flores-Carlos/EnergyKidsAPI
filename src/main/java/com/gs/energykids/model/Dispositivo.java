package com.gs.energykids.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "DISPOSITIVO")
public class Dispositivo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "DISPOSITIVO_ID")
    private Long id;

    @NotBlank(message = "O nome do dispositivo é obrigatório")
    @Column(name = "NOME", nullable = false)
    private String nome;

    @NotNull(message = "A potência é obrigatória")
    @Column(name = "POTENCIA_WATTS", nullable = false)
    private Double potenciaWatts;

    @NotNull(message = "O consumo diário é obrigatório")
    @Column(name = "CONSUMO_HORAS_DIA", nullable = false)
    private Double consumoHorasDia;

    @NotNull(message = "O usuário associado é obrigatório")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USUARIO_ID", nullable = false)
    private Usuario usuario;

    public Dispositivo() {
    }

    public Dispositivo(String nome, Double potenciaWatts, Double consumoHorasDia, Usuario usuario) {
        this.nome = nome;
        this.potenciaWatts = potenciaWatts;
        this.consumoHorasDia = consumoHorasDia;
        this.usuario = usuario;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Double getPotenciaWatts() {
        return potenciaWatts;
    }

    public void setPotenciaWatts(Double potenciaWatts) {
        this.potenciaWatts = potenciaWatts;
    }

    public Double getConsumoHorasDia() {
        return consumoHorasDia;
    }

    public void setConsumoHorasDia(Double consumoHorasDia) {
        this.consumoHorasDia = consumoHorasDia;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    @Override
    public String toString() {
        return "Dispositivo{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", potenciaWatts=" + potenciaWatts +
                ", consumoHorasDia=" + consumoHorasDia +
                ", usuario=" + usuario +
                '}';
    }
}
