package com.example.demo.infraestructura.seguridad;
import org.springframework.context.annotation.*;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import static org.springframework.security.config.Customizer.withDefaults;
@Configuration
public class SecurityConfig {
    @Bean public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http.csrf(csrf -> csrf.disable())
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(org.springframework.http.HttpMethod.POST,"/api/auth/login","/api/auth/register").permitAll()
                .requestMatchers("/api/usuarios", "/api/usuarios/**").denyAll()
                .anyRequest().authenticated())
            .oauth2ResourceServer(oauth -> oauth.jwt(withDefaults()))
            .build();
    }
}
