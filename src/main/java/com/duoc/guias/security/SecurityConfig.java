package com.duoc.guias.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/h2-console/**").permitAll()

                // Rol solo descarga
                .requestMatchers("/api/guias/*/archivo").hasAnyRole("DESCARGA", "ADMIN")

                // Rol administrador/resto endpoints
                .requestMatchers("/api/guias/**").hasRole("ADMIN")

                .anyRequest().authenticated()
            );
            // .oauth2ResourceServer(oauth2 -> oauth2.jwt());

        http.headers(headers -> headers.frameOptions(frame -> frame.disable()));

        return http.build();
    }
}