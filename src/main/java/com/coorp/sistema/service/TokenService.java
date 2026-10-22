package com.coorp.sistema.service;

import com.coorp.sistema.model.Usuario;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

@Service
public class TokenService {

    // Chave secreta para assinar o token (em produção, use uma chave forte e em variável de ambiente)
    private final SecretKey key = Keys.secretKeyFor(io.jsonwebtoken.SignatureAlgorithm.HS256);
    private final long expirationTime = 86400000; // 24 horas em milissegundos

    public String gerarToken(Usuario usuario) {
        return Jwts.builder()
                .subject(usuario.getLogin())              // Atualizado para 0.12+
                .claim("role", usuario.getRole())
                .issuedAt(new Date())                     // Atualizado para 0.12+
                .expiration(new Date(System.currentTimeMillis() + expirationTime)) // Atualizado
                .signWith(key)
                .compact();
    }

    public boolean validarToken(String token) {
        try {
            // Sintaxe atualizada: parser() em vez de parserBuilder()
            Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public String getLogin(String token) {
        // Sintaxe atualizada
        Claims claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return claims.getSubject();
    }
}