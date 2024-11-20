package com.gs.energykids.controller;

import com.gs.energykids.exception.ResourceNotFoundException;
import com.gs.energykids.model.Consumo;
import com.gs.energykids.repository.ConsumoRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.List;

@RestController
@RequestMapping("/consumos")
public class ConsumoController {

    @Autowired
    private ConsumoRepository consumoRepository;

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
    public ResponseEntity<Consumo> criarConsumo(@Valid @RequestBody Consumo consumo) {
        Consumo salvo = consumoRepository.save(consumo);
        return ResponseEntity.ok(salvo);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Consumo> atualizarConsumo(@PathVariable Long id, @Valid @RequestBody Consumo consumo) {
        Consumo existente = consumoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Consumo com ID " + id + " não encontrado."));
        existente.setDataConsumo(consumo.getDataConsumo());
        existente.setEnergiaConsumidaKwh(consumo.getEnergiaConsumidaKwh());
        Consumo atualizado = consumoRepository.save(existente);
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
