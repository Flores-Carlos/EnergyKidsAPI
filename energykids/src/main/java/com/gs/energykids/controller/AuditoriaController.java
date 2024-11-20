package com.gs.energykids.controller;

import com.gs.energykids.exception.ResourceNotFoundException;
import com.gs.energykids.model.Auditoria;
import com.gs.energykids.repository.AuditoriaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.List;

@RestController
@RequestMapping("/auditorias")
public class AuditoriaController {

    @Autowired
    private AuditoriaRepository auditoriaRepository;

    @GetMapping
    public ResponseEntity<Page<Auditoria>> listarAuditorias(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "dataHora,desc") String sort) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sort.split(",")[0]).ascending());
        if (sort.endsWith(",desc")) {
            pageable = PageRequest.of(page, size, Sort.by(sort.split(",")[0]).descending());
        }
        Page<Auditoria> auditorias = auditoriaRepository.findAll(pageable);
        return ResponseEntity.ok(auditorias);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Auditoria> buscarAuditoriaPorId(@PathVariable Long id) {
        Auditoria auditoria = auditoriaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Auditoria com ID " + id + " não encontrada."));
        return ResponseEntity.ok(auditoria);
    }
}
