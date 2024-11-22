package com.gs.energykids.controller;

import com.gs.energykids.exception.ResourceNotFoundException;
import com.gs.energykids.model.Usuario;
import com.gs.energykids.repository.UsuarioRepository;
import com.gs.energykids.service.EmailProducer;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    private static final Logger logger = LoggerFactory.getLogger(UsuarioController.class);

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private EmailProducer emailProducer;

    @GetMapping
    public ResponseEntity<Page<Usuario>> listarUsuarios(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "nome,asc") String sort) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sort.split(",")[0]).ascending());
        if (sort.endsWith(",desc")) {
            pageable = PageRequest.of(page, size, Sort.by(sort.split(",")[0]).descending());
        }

        Page<Usuario> usuarios = usuarioRepository.findAll(pageable);
        logger.info("Listando usuários - Página: {}, Tamanho: {}, Ordenação: {}", page, size, sort);
        return ResponseEntity.ok(usuarios);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Usuario> buscarUsuarioPorId(@PathVariable Long id) {
        logger.info("Buscando usuário com ID: {}", id);
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Usuário com ID {} não encontrado", id);
                    return new ResourceNotFoundException("Usuário com ID " + id + " não encontrado.");
                });
        return ResponseEntity.ok(usuario);
    }

    @PostMapping
    public ResponseEntity<Usuario> criarUsuario(@Valid @RequestBody Usuario usuario) {
        Usuario usuarioSalvo = usuarioRepository.save(usuario);

        emailProducer.sendEmailMessage(
                usuarioSalvo.getEmail(),
                "Bem-vindo ao Energy Kids",
                "Olá " + usuarioSalvo.getNome() + ", seu cadastro foi realizado com sucesso!"
        );
        logger.info("Usuário criado: {}", usuarioSalvo);

        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioSalvo);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Usuario> atualizarUsuario(@PathVariable Long id, @Valid @RequestBody Usuario usuario) {
        Usuario existente = usuarioRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Usuário com ID {} não encontrado", id);
                    return new ResourceNotFoundException("Usuário com ID " + id + " não encontrado.");
                });
        existente.setNome(usuario.getNome());
        existente.setEmail(usuario.getEmail());
        existente.setSenhaHash(usuario.getSenhaHash());
        Usuario atualizado = usuarioRepository.save(existente);

        logger.info("Usuário com ID {} atualizado: {}", id, atualizado);
        return ResponseEntity.ok(atualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluirUsuario(@PathVariable Long id) {
        Usuario existente = usuarioRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Usuário com ID {} não encontrado", id);
                    return new ResourceNotFoundException("Usuário com ID " + id + " não encontrado.");
                });
        usuarioRepository.delete(existente);

        logger.info("Usuário com ID {} excluído.", id);
        return ResponseEntity.noContent().build();
    }
}
