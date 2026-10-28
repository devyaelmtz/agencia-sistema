package com.example.demo.infraestructura.seguridad;
import org.springframework.context.annotation.*;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import static org.springframework.security.config.Customizer.withDefaults;
@Configuration
public class SecurityConfig {
    @Bean org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter roleConverter() {
        var roles = new org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter();
        roles.setAuthoritiesClaimName("roles"); roles.setAuthorityPrefix("");
        var converter = new org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(roles); return converter;
    }
    @Bean org.springframework.web.cors.CorsConfigurationSource corsConfigurationSource() {
        var config = new org.springframework.web.cors.CorsConfiguration();
        var origins = new java.util.ArrayList<String>();
        for (String scheme : java.util.List.of("http://", "https://")) {
            for (String host : java.util.List.of("localhost", "127.0.0.1", "192.168.*", "10.*", "*.miagencia.local", "miagencia.local")) origins.add(scheme+host+":[*]");
            for (int i=16;i<=31;i++) origins.add(scheme+"172."+i+".*:[*]");
        }
        config.setAllowedOriginPatterns(origins);
        config.setAllowedMethods(java.util.List.of("GET","POST","PUT","DELETE","OPTIONS"));
        config.setAllowedHeaders(java.util.List.of("Authorization","Content-Type"));
        config.setExposedHeaders(java.util.List.of("Location"));
        var source = new org.springframework.web.cors.UrlBasedCorsConfigurationSource(); source.registerCorsConfiguration("/**",config); return source;
    }
    @Bean public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http.cors(withDefaults()).csrf(csrf -> csrf.disable())
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(org.springframework.http.HttpMethod.POST,"/api/auth/login","/api/auth/register","/api/auth/forgot-password","/api/auth/reset-password").permitAll()
                .requestMatchers("/api/usuarios", "/api/usuarios/**").denyAll()
                .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/vehicles", "/api/vehicles/**").authenticated()
                .requestMatchers("/api/vehicles", "/api/vehicles/**", "/api/admin/**", "/api/vehiculos", "/api/vehiculos/**").hasRole("ADMIN")
                .anyRequest().authenticated())
            .oauth2ResourceServer(oauth -> oauth.jwt(jwt -> jwt.jwtAuthenticationConverter(roleConverter())))
            .build();
    }
}
