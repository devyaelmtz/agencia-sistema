package com.example.demo.infraestructura.adaptadores.entrada.rest;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.*;
import java.util.Map;
@RestController @RequestMapping("/api/auth")
public class PasswordResetController {
    private final com.example.demo.aplicacion.servicio.PasswordResetService service;
    public PasswordResetController(com.example.demo.aplicacion.servicio.PasswordResetService service) {this.service=service;}
    public record Forgot(String correo) {} public record Reset(String token,String password) {}
    @PostMapping("/forgot-password") public ResponseEntity<?> forgot(@RequestBody Forgot r) {
        try { service.solicitar(r.correo()); } catch(org.springframework.mail.MailException e) {
            // La respuesta pública no revela si el correo está registrado ni expone datos SMTP.
            org.slf4j.LoggerFactory.getLogger(getClass()).warn("No se pudo entregar el correo de recuperación; revisa SMTP.");
        }
        return ResponseEntity.accepted().body(Map.of("message","Si el correo está registrado, recibirás instrucciones para recuperar tu acceso."));
    }
    @PostMapping("/reset-password") public Map<String,String> reset(@RequestBody Reset r) {service.reset(r.token(),r.password());return Map.of("message","Contraseña actualizada. Ya puedes iniciar sesión.");}
    @ExceptionHandler(IllegalArgumentException.class) public ResponseEntity<?> invalid(IllegalArgumentException e) {return ResponseEntity.badRequest().body(Map.of("message",e.getMessage()));}
}
