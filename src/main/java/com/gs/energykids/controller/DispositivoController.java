package com.gs.energykids.controller;

import com.gs.energykids.exception.ResourceNotFoundException;
import com.gs.energykids.model.Dispositivo;
import com.gs.energykids.repository.DispositivoRepository;
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
@RequestMapping("/dispositivos")
public class DispositivoController {

    @Autowired
    private DispositivoRepository dispositivoRepository;

    @GetMapping
    public ResponseEntity<Page<Dispositivo>> listarDispositivos(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "nome,asc") String sort) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sort.split(",")[0]).ascending());
        if (sort.endsWith(",desc")) {
            pageable = PageRequest.of(page, size, Sort.by(sort.split(",")[0]).descending());
        }
        Page<Dispositivo> dispositivos = dispositivoRepository.findAll(pageable);
        return ResponseEntity.ok(dispositivos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Dispositivo> buscarDispositivoPorId(@PathVariable Long id) {
        Dispositivo dispositivo = dispositivoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Dispositivo com ID " + id + " não encontrado."));
        return ResponseEntity.ok(dispositivo);
    }

    @PostMapping
    public ResponseEntity<Dispositivo> criarDispositivo(@Valid @RequestBody Dispositivo dispositivo) {
        Dispositivo salvo = dispositivoRepository.save(dispositivo);
        return ResponseEntity.ok(salvo);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Dispositivo> atualizarDispositivo(@PathVariable Long id, @Valid @RequestBody Dispositivo dispositivo) {
        Dispositivo existente = dispositivoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Dispositivo com ID " + id + " não encontrado."));
        existente.setNome(dispositivo.getNome());
        existente.setPotenciaWatts(dispositivo.getPotenciaWatts());
        existente.setConsumoHorasDia(dispositivo.getConsumoHorasDia());
        Dispositivo atualizado = dispositivoRepository.save(existente);
        return ResponseEntity.ok(atualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluirDispositivo(@PathVariable Long id) {
        Dispositivo existente = dispositivoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Dispositivo com ID " + id + " não encontrado."));
        dispositivoRepository.delete(existente);
        return ResponseEntity.noContent().build();
    }
}
