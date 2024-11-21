package com.gs.energykids.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

@Entity
@Table(name = "CONSUMO")
public class Consumo {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "consumo_seq")
    @SequenceGenerator(name = "consumo_seq", sequenceName = "SEQ_CONSUMO", allocationSize = 1)
    @Column(name = "CONSUMO_ID")
    private Long id;

    @NotNull(message = "O dispositivo é obrigatório")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "DISPOSITIVO_ID", nullable = false)
    private Dispositivo dispositivo;

    @NotNull(message = "A data de consumo é obrigatória")
    @Column(name = "DATA_REGISTRO", nullable = false)
    private LocalDate dataConsumo;

    @NotNull(message = "A energia consumida é obrigatória")
    @Column(name = "CONSUMO_MENSAL_KWH", nullable = false)
    private Double energiaConsumidaKwh;

    public Consumo() {
    }

    public Consumo(Dispositivo dispositivo, LocalDate dataConsumo, Double energiaConsumidaKwh) {
        this.dispositivo = dispositivo;
        this.dataConsumo = dataConsumo;
        this.energiaConsumidaKwh = energiaConsumidaKwh;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Dispositivo getDispositivo() {
        return dispositivo;
    }

    public void setDispositivo(Dispositivo dispositivo) {
        this.dispositivo = dispositivo;
    }

    public LocalDate getDataConsumo() {
        return dataConsumo;
    }

    public void setDataConsumo(LocalDate dataConsumo) {
        this.dataConsumo = dataConsumo;
    }

    public Double getEnergiaConsumidaKwh() {
        return energiaConsumidaKwh;
    }

    public void setEnergiaConsumidaKwh(Double energiaConsumidaKwh) {
        this.energiaConsumidaKwh = energiaConsumidaKwh;
    }

    @Override
    public String toString() {
        return "Consumo{" +
                "id=" + id +
                ", dispositivo=" + dispositivo +
                ", dataConsumo=" + dataConsumo +
                ", energiaConsumidaKwh=" + energiaConsumidaKwh +
                '}';
    }
}
