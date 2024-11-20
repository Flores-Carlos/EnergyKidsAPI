package com.gs.energykids.controller;

import com.gs.energykids.exception.ResourceNotFoundException;
import com.gs.energykids.model.DicaEconomia;
import com.gs.energykids.repository.DicaEconomiaRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/dicas")
public class DicaEconomiaController {

    @Autowired
    private DicaEconomiaRepository dicaEconomiaRepository;

    @GetMapping
    public ResponseEntity<Page<DicaEconomia>> listarDicas(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "dataCriacao,asc") String sort) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sort.split(",")[0]).ascending());
        if (sort.endsWith(",desc")) {
            pageable = PageRequest.of(page, size, Sort.by(sort.split(",")[0]).descending());
        }
        Page<DicaEconomia> dicas = dicaEconomiaRepository.findAll(pageable);
        return ResponseEntity.ok(dicas);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DicaEconomia> buscarDicaPorId(@PathVariable Long id) {
        DicaEconomia dica = dicaEconomiaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Dica com ID " + id + " não encontrada."));
        return ResponseEntity.ok(dica);
    }

    @PostMapping
    public ResponseEntity<DicaEconomia> criarDica(@Valid @RequestBody DicaEconomia dica) {
        dica.setDataCriacao(LocalDate.now());
        DicaEconomia salva = dicaEconomiaRepository.save(dica);
        return ResponseEntity.ok(salva);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DicaEconomia> atualizarDica(@PathVariable Long id, @Valid @RequestBody DicaEconomia dica) {
        DicaEconomia existente = dicaEconomiaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Dica com ID " + id + " não encontrada."));
        existente.setDescricao(dica.getDescricao());
        DicaEconomia atualizada = dicaEconomiaRepository.save(existente);
        return ResponseEntity.ok(atualizada);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluirDica(@PathVariable Long id) {
        DicaEconomia existente = dicaEconomiaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Dica com ID " + id + " não encontrada."));
        dicaEconomiaRepository.delete(existente);
        return ResponseEntity.noContent().build();
    }
}
