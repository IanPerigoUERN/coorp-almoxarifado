package com.coorp.sistema;

import com.coorp.sistema.JwtFilter;
import com.coorp.sistema.model.Usuario;
import com.coorp.sistema.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private JwtFilter jwtFilter; // 👈 Injetando o nosso filtro

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // 1. Arquivos estáticos: liberados para todos
                .requestMatchers("/", "/index.html", "/style.css", "/app.js",
                                 "/login.html", "/login.js", "/consulta.html", "/consulta.js").permitAll()

                // 2. Rota de login: liberada para todos
                .requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll()

                // 3. Exemplo de autorização por ROLE (Opcional, mas muito bom para a nota!)
                // Apenas ADMIN pode deletar produtos. Os outros precisam estar logados.
                .requestMatchers(HttpMethod.DELETE, "/api/produtos/**").hasRole("ADMIN")
                
                
                // Relatório
               // .requestMatchers("/api/relatorios/**").permitAll()

                // 4. Qualquer outra requisição para /api/**: exige autenticação (ter um token válido)
                .requestMatchers("/api/**").authenticated()
                
                
              

                // 5. Qualquer outra coisa: exige autenticação
                .anyRequest().authenticated()
            )
            // Adiciona o nosso filtro antes do filtro padrão de autenticação do Spring
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CommandLineRunner initData() {
        return args -> {
            // Cria o ADMIN se não existir
            if (usuarioRepository.findByLogin("admin").isEmpty()) {
                Usuario admin = new Usuario("admin", passwordEncoder().encode("admin123"), "ADMIN");
                usuarioRepository.save(admin);
                System.out.println(">>> Usuário 'admin' criado com senha 'admin123'");
            }
            
            // 👇 ADICIONE ESTE BLOCO PARA CRIAR O USUÁRIO COMUM 👇
            if (usuarioRepository.findByLogin("comum").isEmpty()) {
                Usuario comum = new Usuario("comum", passwordEncoder().encode("123456"), "USER");
                usuarioRepository.save(comum);
                System.out.println(">>> Usuário 'comum' criado com senha '123456'");
            }
        };
    }
}
