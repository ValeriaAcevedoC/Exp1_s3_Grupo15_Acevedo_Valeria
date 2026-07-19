package com.duoc.guias.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
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

                // Consola H2 para pruebas locales
                .requestMatchers("/h2-console/**").permitAll()

                // Rol DESCARGA: solo puede descargar guías
                .requestMatchers(HttpMethod.GET, "/api/guias/*/archivo")
                    .hasAnyRole("DESCARGA", "ADMIN")

                // Rol ADMIN: puede consultar guías
                .requestMatchers(HttpMethod.GET, "/api/guias/**")
                    .hasRole("ADMIN")

                // Rol ADMIN: puede crear guías
                .requestMatchers(HttpMethod.POST, "/api/guias")
                    .denyAll()

                // Rol ADMIN: puede actualizar guías
                .requestMatchers(HttpMethod.PUT, "/api/guias/**")
                    .hasRole("ADMIN")

                // Rol ADMIN: puede eliminar guías
                .requestMatchers(HttpMethod.DELETE, "/api/guias/**")
                    .hasRole("ADMIN")
                
                // Deniega el acceso a rabbit para producir mensajes, este era un endpoint de prueba.
                .requestMatchers("/api/rabbit/enviar").denyAll()
                
                // Rol ADMIN: puede consumir guías a través de RabbitMQ
                .requestMatchers("/api/rabbit/consumir").hasRole("ADMIN")

                .anyRequest().authenticated()
            )
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> jwt.jwtAuthenticationConverter(new AzureRoleConverter()))
            );

        http.headers(headers -> headers.frameOptions(frame -> frame.disable()));

        return http.build();
    }
}
