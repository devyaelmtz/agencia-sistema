package com.example.demo.infraestructura.seguridad;
import org.springframework.context.annotation.*;
import org.springframework.beans.factory.annotation.Value;
@Configuration
public class AdminBootstrap {
    @Bean org.springframework.boot.CommandLineRunner bootstrapAdmin(com.example.demo.aplicacion.servicio.AdminRoleService roles, @Value("${ADMIN_BOOTSTRAP_EMAIL:}") String email) {
        return args -> { if (!email.isBlank()) roles.bootstrap(email); };
    }
}
