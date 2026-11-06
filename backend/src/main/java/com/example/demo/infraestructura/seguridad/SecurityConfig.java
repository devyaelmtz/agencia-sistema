package com.example.demo.infraestructura.seguridad;

import static org.springframework.security.config.Customizer.withDefaults;

import org.springframework.context.annotation.*;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {
  @Bean
  org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter
      roleConverter() {
    var roles =
        new org.springframework.security.oauth2.server.resource.authentication
            .JwtGrantedAuthoritiesConverter();
    roles.setAuthoritiesClaimName("roles");
    roles.setAuthorityPrefix("");
    var converter =
        new org.springframework.security.oauth2.server.resource.authentication
            .JwtAuthenticationConverter();
    converter.setJwtGrantedAuthoritiesConverter(roles);
    return converter;
  }

  @Bean
  org.springframework.web.cors.CorsConfigurationSource corsConfigurationSource() {
    var config = new org.springframework.web.cors.CorsConfiguration();
    var origins = new java.util.ArrayList<String>();
    for (String scheme : java.util.List.of("http://", "https://")) {
      for (String host :
          java.util.List.of(
              "localhost",
              "127.0.0.1",
              "192.168.*",
              "10.*",
              "*.miagencia.local",
              "miagencia.local",
              "agencia.local",
              "api.agencia.local")) origins.add(scheme + host + ":[*]");
      for (int i = 16; i <= 31; i++) origins.add(scheme + "172." + i + ".*:[*]");
    }
    config.setAllowedOriginPatterns(origins);
    config.setAllowedMethods(java.util.List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
    config.setAllowedHeaders(java.util.List.of("Authorization", "Content-Type", "Accept"));
    config.setExposedHeaders(java.util.List.of("Location"));
    var source = new org.springframework.web.cors.UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", config);
    return source;
  }

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    return http.cors(withDefaults())
        .csrf(c -> c.disable())
        .formLogin(c -> c.disable())
        .httpBasic(c -> c.disable())
        .requestCache(c -> c.disable())
        .sessionManagement(c -> c.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(
            a ->
                a.dispatcherTypeMatchers(jakarta.servlet.DispatcherType.ERROR)
                    .permitAll()
                    .requestMatchers(HttpMethod.OPTIONS, "/**")
                    .permitAll()
                    .requestMatchers(
                        HttpMethod.POST,
                        "/api/auth/login",
                        "/api/auth/register",
                        "/api/auth/forgot-password",
                        "/api/auth/reset-password")
                    .permitAll()
                    .requestMatchers("/api/admin/**", "/api/ventas/**", "/api/ventas")
                    .hasRole("ADMIN")
                    .requestMatchers(HttpMethod.GET, "/api/vehicles", "/api/vehicles/**")
                    .authenticated()
                    .requestMatchers("/api/vehicles", "/api/vehicles/**")
                    .hasRole("ADMIN")
                    .requestMatchers(
                        "/api/auth/me",
                        "/api/usuarios",
                        "/api/usuarios/**",
                        "/api/clientes",
                        "/api/clientes/**",
                        "/api/arco-requests",
                        "/api/arco-requests/**",
                        "/api/arco/my-requests")
                    .authenticated()
                    .requestMatchers("/api/**")
                    .hasRole("ADMIN")
                    .anyRequest()
                    .denyAll())
        .oauth2ResourceServer(o -> o.jwt(j -> j.jwtAuthenticationConverter(roleConverter())))
        .build();
  }
}
