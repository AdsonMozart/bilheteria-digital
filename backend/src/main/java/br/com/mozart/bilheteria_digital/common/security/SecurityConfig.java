package br.com.mozart.bilheteria_digital.common.security;

import br.com.mozart.bilheteria_digital.common.exception.ApiErroResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;
    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    public SecurityConfig(JwtAuthFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint((request, response, authException) -> responderErro(
                                response,
                                HttpStatus.UNAUTHORIZED,
                                "Nao autenticado",
                                "Informe um token valido para acessar este recurso",
                                request.getRequestURI()
                        ))
                        .accessDeniedHandler((request, response, accessDeniedException) -> responderErro(
                                response,
                                HttpStatus.FORBIDDEN,
                                "Acesso negado",
                                "Voce nao tem permissao para acessar este recurso",
                                request.getRequestURI()
                        )))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/webhooks/stripe", "/api/webhooks/stripe/").permitAll()
                        .requestMatchers("/error").permitAll()
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()

                        .requestMatchers("/api/usuarios/me").authenticated()

                        .requestMatchers("/api/organizador/**").hasRole("ORGANIZADOR")
                        .requestMatchers("/api/catalogo/**").hasRole("ORGANIZADOR")
                        .requestMatchers("/api/cliente/**").hasRole("CLIENTE")
                        .requestMatchers("/api/portaria/**").hasRole("PORTARIA")
                        .requestMatchers(HttpMethod.GET, "/api/eventos/**").permitAll()
                        .requestMatchers("/api/reservas/**").hasRole("CLIENTE")
                        .requestMatchers("/api/pagamentos/**").hasRole("CLIENTE")
                        .requestMatchers("/api/me/ingressos/**").hasRole("CLIENTE")
                        .requestMatchers(HttpMethod.GET, "/api/ingressos/compartilhado/**").permitAll()
                        .anyRequest().authenticated())
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    private void responderErro(
            jakarta.servlet.http.HttpServletResponse response,
            HttpStatus status,
            String erro,
            String mensagem,
            String path
    ) throws java.io.IOException {
        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        objectMapper.writeValue(response.getWriter(), ApiErroResponse.semCampos(status.value(), erro, mensagem, path));
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) {
        return configuration.getAuthenticationManager();
    }

}
