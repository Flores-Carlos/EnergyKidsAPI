package com.gs.energykids.controller;

import com.gs.energykids.exception.ResourceNotFoundException;
import com.gs.energykids.model.DicaEconomia;
import com.gs.energykids.repository.DicaEconomiaRepository;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

@RestController
@RequestMapping("/dicas")
public class DicaEconomiaController {

    private static final Logger logger = LoggerFactory.getLogger(DicaEconomiaController.class);

    @Autowired
    private DicaEconomiaRepository dicaEconomiaRepository;

    @GetMapping
    public ResponseEntity<Page<DicaEconomia>> listarDicas(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id,asc") String sort) { // Ordenação ajustada para usar o campo "id"
        Pageable pageable = PageRequest.of(page, size, Sort.by(sort.split(",")[0]).ascending());
        if (sort.endsWith(",desc")) {
            pageable = PageRequest.of(page, size, Sort.by(sort.split(",")[0]).descending());
        }

        Page<DicaEconomia> dicas = dicaEconomiaRepository.findAll(pageable);
        logger.info("Listando dicas - Página: {}, Tamanho: {}, Ordenação: {}", page, size, sort);
        return ResponseEntity.ok(dicas);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DicaEconomia> buscarDicaPorId(@PathVariable Long id) {
        logger.info("Buscando dica com ID: {}", id);
        DicaEconomia dica = dicaEconomiaRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Dica com ID {} não encontrada", id);
                    return new ResourceNotFoundException("Dica com ID " + id + " não encontrada.");
                });
        return ResponseEntity.ok(dica);
    }

    @PostMapping
    public ResponseEntity<DicaEconomia> criarDica(@Valid @RequestBody DicaEconomia dica) {
        DicaEconomia salva = dicaEconomiaRepository.save(dica);
        logger.info("Dica criada: {}", salva);
        return ResponseEntity.ok(salva);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DicaEconomia> atualizarDica(@PathVariable Long id, @Valid @RequestBody DicaEconomia dica) {
        logger.info("Atualizando dica com ID: {}", id);
        DicaEconomia existente = dicaEconomiaRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Dica com ID {} não encontrada para atualização", id);
                    return new ResourceNotFoundException("Dica com ID " + id + " não encontrada.");
                });

        existente.setDescricao(dica.getDescricao());
        existente.setCategoria(dica.getCategoria());
        existente.setRelevancia(dica.getRelevancia());
        DicaEconomia atualizada = dicaEconomiaRepository.save(existente);
        logger.info("Dica com ID {} atualizada: {}", id, atualizada);
        return ResponseEntity.ok(atualizada);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluirDica(@PathVariable Long id) {
        logger.info("Excluindo dica com ID: {}", id);
        DicaEconomia existente = dicaEconomiaRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Dica com ID {} não encontrada para exclusão", id);
                    return new ResourceNotFoundException("Dica com ID " + id + " não encontrada.");
                });
        dicaEconomiaRepository.delete(existente);
        logger.info("Dica com ID {} excluída.", id);
        return ResponseEntity.noContent().build();
    }
}
