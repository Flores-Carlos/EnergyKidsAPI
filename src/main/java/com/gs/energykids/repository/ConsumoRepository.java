package com.gs.energykids.repository;

import com.gs.energykids.model.Consumo;
import com.gs.energykids.model.Dispositivo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConsumoRepository extends JpaRepository<Consumo, Long> {
    List<Consumo> findByDispositivoId(Long dispositivoId);

}
