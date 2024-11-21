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

        // Enviar mensagem para RabbitMQ
        emailProducer.sendEmailMessage(
                usuarioSalvo.getEmail(),
                "Bem-vindo ao Energy Kids",
                "Olá " + usuarioSalvo.getNome() + ", seu cadastro foi realizado com sucesso!"
        );
        logger.info("Usuário criado: {}", usuarioSalvo);

        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioSalvo);
    }

    @PostMapping("/login")
    public ResponseEntity<Void> login(@RequestBody Map<String, String> credentials) {
        String email = credentials.get("email");
        String senha = credentials.get("senha");

        logger.info("Tentativa de login com o email: {}", email);

        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> {
                    logger.error("Usuário com email {} não encontrado", email);
                    return new ResourceNotFoundException("Usuário com email " + email + " não encontrado.");
                });

        if (!usuario.getSenhaHash().equals(senha)) {
            logger.warn("Senha inválida para o email: {}", email);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // Enviar mensagem para RabbitMQ
        emailProducer.sendEmailMessage(
                usuario.getEmail(),
                "Login realizado com sucesso",
                "Olá " + usuario.getNome() + ", você acabou de fazer login no Energy Kids."
        );
        logger.info("Login realizado com sucesso para o email: {}", email);

        return ResponseEntity.ok().build();
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
