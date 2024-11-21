package com.gs.energykids.controller;

import com.gs.energykids.model.Dispositivo;
import com.gs.energykids.model.Usuario;
import com.gs.energykids.repository.DispositivoRepository;
import com.gs.energykids.repository.UsuarioRepository;
import com.gs.energykids.exception.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/dispositivos")
public class DispositivoController {

    private static final Logger logger = LoggerFactory.getLogger(DispositivoController.class);

    private final DispositivoRepository dispositivoRepository;
    private final UsuarioRepository usuarioRepository;

    public DispositivoController(DispositivoRepository dispositivoRepository, UsuarioRepository usuarioRepository) {
        this.dispositivoRepository = dispositivoRepository;
        this.usuarioRepository = usuarioRepository;
    }

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
        logger.info("Listando dispositivos - Página: {}, Tamanho: {}, Ordenação: {}", page, size, sort);
        return ResponseEntity.ok(dispositivos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Dispositivo> buscarDispositivoPorId(@PathVariable Long id) {
        logger.info("Buscando dispositivo com ID: {}", id);
        Dispositivo dispositivo = dispositivoRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Dispositivo com ID {} não encontrado", id);
                    return new ResourceNotFoundException("Dispositivo com ID " + id + " não encontrado.");
                });
        return ResponseEntity.ok(dispositivo);
    }

    @PostMapping
    public ResponseEntity<Dispositivo> criarDispositivo(@RequestBody Map<String, Object> request) {
        Long usuarioId = Long.valueOf(request.get("usuarioId").toString());
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> {
                    logger.error("Usuário com ID {} não encontrado ao criar dispositivo.", usuarioId);
                    return new ResourceNotFoundException("Usuário com ID " + usuarioId + " não encontrado.");
                });

        Dispositivo dispositivo = new Dispositivo();
        dispositivo.setNome(request.get("nome").toString());
        dispositivo.setPotenciaWatts(Double.valueOf(request.get("potenciaWatts").toString()));
        dispositivo.setConsumoHorasDia(Double.valueOf(request.get("consumoHorasDia").toString()));
        dispositivo.setUsuario(usuario);

        Dispositivo salvo = dispositivoRepository.save(dispositivo);
        logger.info("Dispositivo criado: {}", salvo);
        return ResponseEntity.status(HttpStatus.CREATED).body(salvo);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Dispositivo> atualizarDispositivo(@PathVariable Long id, @RequestBody Map<String, Object> request) {
        logger.info("Atualizando dispositivo com ID: {}", id);
        Dispositivo dispositivoExistente = dispositivoRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Dispositivo com ID {} não encontrado para atualização", id);
                    return new ResourceNotFoundException("Dispositivo com ID " + id + " não encontrado.");
                });

        if (request.containsKey("nome")) {
            dispositivoExistente.setNome(request.get("nome").toString());
        }
        if (request.containsKey("potenciaWatts")) {
            dispositivoExistente.setPotenciaWatts(Double.valueOf(request.get("potenciaWatts").toString()));
        }
        if (request.containsKey("consumoHorasDia")) {
            dispositivoExistente.setConsumoHorasDia(Double.valueOf(request.get("consumoHorasDia").toString()));
        }
        if (request.containsKey("usuarioId")) {
            Long usuarioId = Long.valueOf(request.get("usuarioId").toString());
            Usuario usuario = usuarioRepository.findById(usuarioId)
                    .orElseThrow(() -> {
                        logger.error("Usuário com ID {} não encontrado ao atualizar dispositivo.", usuarioId);
                        return new ResourceNotFoundException("Usuário com ID " + usuarioId + " não encontrado.");
                    });
            dispositivoExistente.setUsuario(usuario);
        }

        Dispositivo atualizado = dispositivoRepository.save(dispositivoExistente);
        logger.info("Dispositivo com ID {} atualizado: {}", id, atualizado);
        return ResponseEntity.ok(atualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluirDispositivo(@PathVariable Long id) {
        logger.info("Excluindo dispositivo com ID: {}", id);
        Dispositivo dispositivo = dispositivoRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Dispositivo com ID {} não encontrado para exclusão.", id);
                    return new ResourceNotFoundException("Dispositivo com ID " + id + " não encontrado.");
                });
        dispositivoRepository.delete(dispositivo);
        logger.info("Dispositivo com ID {} excluído.", id);
        return ResponseEntity.noContent().build();
    }
}
