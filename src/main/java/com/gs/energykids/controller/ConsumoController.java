package com.gs.energykids.controller;

import com.gs.energykids.exception.ResourceNotFoundException;
import com.gs.energykids.model.Consumo;
import com.gs.energykids.model.Dispositivo;
import com.gs.energykids.repository.ConsumoRepository;
import com.gs.energykids.repository.DispositivoRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/consumos")
public class ConsumoController {

    @Autowired
    private ConsumoRepository consumoRepository;

    @Autowired
    private DispositivoRepository dispositivoRepository; // Corrigido para usar @Autowired

    @GetMapping
    public ResponseEntity<Page<Consumo>> listarConsumos(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "dataConsumo,asc") String sort) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sort.split(",")[0]).ascending());
        if (sort.endsWith(",desc")) {
            pageable = PageRequest.of(page, size, Sort.by(sort.split(",")[0]).descending());
        }
        Page<Consumo> consumos = consumoRepository.findAll(pageable);
        return ResponseEntity.ok(consumos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Consumo> buscarConsumoPorId(@PathVariable Long id) {
        Consumo consumo = consumoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Consumo com ID " + id + " não encontrado."));
        return ResponseEntity.ok(consumo);
    }

    @PostMapping
    public ResponseEntity<Consumo> criarConsumo(@RequestBody Map<String, Object> request) {
        Long dispositivoId = Long.valueOf(request.get("dispositivoId").toString());
        Dispositivo dispositivo = dispositivoRepository.findById(dispositivoId)
                .orElseThrow(() -> new ResourceNotFoundException("Dispositivo com ID " + dispositivoId + " não encontrado."));

        Consumo consumo = new Consumo();
        consumo.setDataConsumo(LocalDate.parse(request.get("dataConsumo").toString()));
        consumo.setEnergiaConsumidaKwh(Double.valueOf(request.get("energiaConsumidaKwh").toString()));
        consumo.setDispositivo(dispositivo);

        Consumo salvo = consumoRepository.save(consumo);
        return ResponseEntity.status(HttpStatus.CREATED).body(salvo);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Consumo> atualizarConsumo(@PathVariable Long id, @RequestBody Map<String, Object> request) {
        Consumo consumoExistente = consumoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Consumo com ID " + id + " não encontrado."));

        if (request.containsKey("dataConsumo")) {
            consumoExistente.setDataConsumo(LocalDate.parse(request.get("dataConsumo").toString()));
        }
        if (request.containsKey("energiaConsumidaKwh")) {
            consumoExistente.setEnergiaConsumidaKwh(Double.valueOf(request.get("energiaConsumidaKwh").toString()));
        }
        if (request.containsKey("dispositivoId")) {
            Long dispositivoId = Long.valueOf(request.get("dispositivoId").toString());
            Dispositivo dispositivo = dispositivoRepository.findById(dispositivoId)
                    .orElseThrow(() -> new ResourceNotFoundException("Dispositivo com ID " + dispositivoId + " não encontrado."));
            consumoExistente.setDispositivo(dispositivo);
        }

        Consumo atualizado = consumoRepository.save(consumoExistente);
        return ResponseEntity.ok(atualizado);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluirConsumo(@PathVariable Long id) {
        Consumo existente = consumoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Consumo com ID " + id + " não encontrado."));
        consumoRepository.delete(existente);
        return ResponseEntity.noContent().build();
    }
}
