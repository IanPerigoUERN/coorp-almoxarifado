package com.coorp.sistema.controller;

import com.coorp.sistema.model.Usuario;
import com.coorp.sistema.repository.UsuarioRepository;
import com.coorp.sistema.service.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    // DTO para receber os dados de login
    public record LoginDTO(String login, String senha) {}
    public record TokenDTO(String token, String role) {}

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDTO data) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findByLogin(data.login());

        if (usuarioOpt.isPresent() && passwordEncoder.matches(data.senha(), usuarioOpt.get().getSenha())) {
            Usuario usuario = usuarioOpt.get();
            String token = tokenService.gerarToken(usuario);
            return ResponseEntity.ok(new TokenDTO(token, usuario.getRole()));
        }

        return ResponseEntity.status(401).body("Login ou senha inválidos");
    }
}