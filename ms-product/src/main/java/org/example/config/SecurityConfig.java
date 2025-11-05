package org.example.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.http.HttpMethod;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

/**
 * Configuración de Seguridad para Spring WebFlux.
 * Esta clase es NECESARIA para que las peticiones OPTIONS (preflight de CORS)
 * no sean bloqueadas por Spring Security con un 403 Forbidden.
 */
@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    /**
     * Define la cadena de filtros de seguridad para la aplicación.
     * @param http Configuración de seguridad.
     * @return El filtro de seguridad configurado.
     */
    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
                // 1. Deshabilita CSRF (común para APIs)
                .csrf(ServerHttpSecurity.CsrfSpec::disable)

                // 2. Habilita CORS y usa el Bean de configuración CorsConfigurationSource
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // 3. Configuración de autorización
                .authorizeExchange(exchanges -> exchanges
                        // [A] PERMITIR OPTIONS: Absolutamente necesario para CORS Preflight.
                        .pathMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // [B] PERMITIR RUTAS DE PRODUCTO: Acceso público al CRUD.
                        .pathMatchers("/products", "/products/**").permitAll()

                        // [C] CUALQUIER OTRA RUTA: Requerir autenticación.
                        .anyExchange().authenticated()
                )

                // 4. Deshabilita la autenticación por defecto (para evitar la contraseña generada)
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)

                .build();
    }

    /**
     * Define un Bean explícito para la configuración de CORS.
     * Esto anula la configuración YAML/Properties y asegura que Spring Security
     * conozca la configuración de CORS inmediatamente.
     */
    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // ** BASADO EN EL YAML PREVIO: Solo se usa "http://localhost:4200" porque allow-credentials=true
        // NO permite el comodín "*". Si se usara "*", allowCredentials debe ser false.
        configuration.setAllowedOrigins(List.of("http://localhost:4200", "https://editor.swagger.io"));

        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L); // 1 hora de caché para preflight

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // Aplicar esta configuración CORS a todas las rutas (/**)
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
