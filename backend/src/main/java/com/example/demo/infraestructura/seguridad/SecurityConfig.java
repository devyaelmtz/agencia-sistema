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
    @Bean public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http.csrf(csrf -> csrf.disable())
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(org.springframework.http.HttpMethod.POST,"/api/auth/login","/api/auth/register").permitAll()
                .requestMatchers("/api/usuarios", "/api/usuarios/**").denyAll()
                .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/vehicles", "/api/vehicles/**").authenticated()
                .requestMatchers("/api/vehicles", "/api/vehicles/**", "/api/admin/**", "/api/vehiculos", "/api/vehiculos/**").hasRole("ADMIN")
                .anyRequest().authenticated())
            .oauth2ResourceServer(oauth -> oauth.jwt(jwt -> jwt.jwtAuthenticationConverter(roleConverter())))
            .build();
    }
}
