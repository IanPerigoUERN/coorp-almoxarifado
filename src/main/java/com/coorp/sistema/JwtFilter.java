package com.coorp.sistema;

import com.coorp.sistema.model.Usuario;
import com.coorp.sistema.repository.UsuarioRepository;
import com.coorp.sistema.service.TokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.Optional;

@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private TokenService tokenService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String header = request.getHeader("Authorization");

        // Verifica se o header existe e começa com "Bearer "
        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);

            if (tokenService.validarToken(token)) {
                String login = tokenService.getLogin(token);
                Optional<Usuario> usuarioOpt = usuarioRepository.findByLogin(login);

                if (usuarioOpt.isPresent()) {
                    Usuario usuario = usuarioOpt.get();

                    // Cria a authority (role) do usuário. O Spring Security espera o prefixo "ROLE_"
                    SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + usuario.getRole());

                    // Cria o objeto de autenticação
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(usuario, null, Collections.singletonList(authority));

                    // 👇 AQUI ESTÁ A MÁGICA: Coloca o usuário no contexto de segurança 👇
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        }

        // Continua a execução da requisição
        filterChain.doFilter(request, response);
    }
}