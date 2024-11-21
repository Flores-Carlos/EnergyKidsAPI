package com.gs.energykids.controller;

import com.gs.energykids.exception.ResourceNotFoundException;
import com.gs.energykids.model.Consumo;
import com.gs.energykids.model.Dispositivo;
import com.gs.energykids.repository.ConsumoRepository;
import com.gs.energykids.repository.DispositivoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger logger = LoggerFactory.getLogger(ConsumoController.class);

    @Autowired
    private ConsumoRepository consumoRepository;

    @Autowired
    private DispositivoRepository dispositivoRepository;

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
        logger.info("Listando consumos - Página: {}, Tamanho: {}, Ordenação: {}", page, size, sort);
        return ResponseEntity.ok(consumos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Consumo> buscarConsumoPorId(@PathVariable Long id) {
        logger.info("Buscando consumo com ID: {}", id);
        Consumo consumo = consumoRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Consumo com ID {} não encontrado", id);
                    return new ResourceNotFoundException("Consumo com ID " + id + " não encontrado.");
                });
        return ResponseEntity.ok(consumo);
    }

    @PostMapping
    public ResponseEntity<Consumo> criarConsumo(@RequestBody Map<String, Object> request) {
        Long dispositivoId = Long.valueOf(request.get("dispositivoId").toString());
        Dispositivo dispositivo = dispositivoRepository.findById(dispositivoId)
                .orElseThrow(() -> {
                    logger.error("Dispositivo com ID {} não encontrado ao criar consumo.", dispositivoId);
                    return new ResourceNotFoundException("Dispositivo com ID " + dispositivoId + " não encontrado.");
                });

        Consumo consumo = new Consumo();
        consumo.setDataConsumo(LocalDate.parse(request.get("dataConsumo").toString()));
        consumo.setEnergiaConsumidaKwh(Double.valueOf(request.get("energiaConsumidaKwh").toString()));
        consumo.setDispositivo(dispositivo);

        Consumo salvo = consumoRepository.save(consumo);
        logger.info("Consumo criado: {}", salvo);
        return ResponseEntity.status(HttpStatus.CREATED).body(salvo);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Consumo> atualizarConsumo(@PathVariable Long id, @RequestBody Map<String, Object> request) {
        logger.info("Atualizando consumo com ID: {}", id);
        Consumo consumoExistente = consumoRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Consumo com ID {} não encontrado para atualização.", id);
                    return new ResourceNotFoundException("Consumo com ID " + id + " não encontrado.");
                });

        if (request.containsKey("dataConsumo")) {
            consumoExistente.setDataConsumo(LocalDate.parse(request.get("dataConsumo").toString()));
        }
        if (request.containsKey("energiaConsumidaKwh")) {
            consumoExistente.setEnergiaConsumidaKwh(Double.valueOf(request.get("energiaConsumidaKwh").toString()));
        }
        if (request.containsKey("dispositivoId")) {
            Long dispositivoId = Long.valueOf(request.get("dispositivoId").toString());
            Dispositivo dispositivo = dispositivoRepository.findById(dispositivoId)
                    .orElseThrow(() -> {
                        logger.error("Dispositivo com ID {} não encontrado ao atualizar consumo.", dispositivoId);
                        return new ResourceNotFoundException("Dispositivo com ID " + dispositivoId + " não encontrado.");
                    });
            consumoExistente.setDispositivo(dispositivo);
        }

        Consumo atualizado = consumoRepository.save(consumoExistente);
        logger.info("Consumo com ID {} atualizado: {}", id, atualizado);
        return ResponseEntity.ok(atualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluirConsumo(@PathVariable Long id) {
        logger.info("Excluindo consumo com ID: {}", id);
        Consumo existente = consumoRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Consumo com ID {} não encontrado para exclusão.", id);
                    return new ResourceNotFoundException("Consumo com ID " + id + " não encontrado.");
                });
        consumoRepository.delete(existente);
        logger.info("Consumo com ID {} excluído.", id);
        return ResponseEntity.noContent().build();
    }
}
