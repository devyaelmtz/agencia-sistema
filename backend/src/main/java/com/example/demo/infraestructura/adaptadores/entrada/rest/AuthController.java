package com.example.demo.infraestructura.adaptadores.entrada.rest;
import com.example.demo.aplicacion.servicio.AuthUseCase;
import com.example.demo.dominio.modelo.Usuario;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthUseCase auth;
    public AuthController(AuthUseCase auth) { this.auth = auth; }
    public record Registro(String nombreUsuario, String correo, String telefono, String password) {}
    public record Login(String correo, String password) {}
    public record Perfil(Long id, String nombreUsuario, String correo, String telefono) {
        static Perfil from(Usuario u) { return new Perfil(u.getId(),u.getNombreUsuario(),u.getCorreo(),u.getTelefono()); }
    }
    @PostMapping("/register")
    public ResponseEntity<Perfil> register(@RequestBody Registro r) {
        return ResponseEntity.status(201).body(Perfil.from(auth.registrar(r.nombreUsuario(),r.correo(),r.telefono(),r.password())));
    }
    @PostMapping("/login")
    public Perfil login(@RequestBody Login r) { return Perfil.from(auth.login(r.correo(),r.password())); }
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> invalid(IllegalArgumentException e) { return ResponseEntity.badRequest().body(Map.of("message",e.getMessage())); }
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<?> duplicate(IllegalStateException e) { return ResponseEntity.status(409).body(Map.of("message",e.getMessage())); }
    @ExceptionHandler(org.springframework.dao.DataIntegrityViolationException.class)
    public ResponseEntity<?> conflict() { return ResponseEntity.status(409).body(Map.of("message","El correo ya está registrado.")); }
    @ExceptionHandler(SecurityException.class)
    public ResponseEntity<?> unauthorized(SecurityException e) { return ResponseEntity.status(401).body(Map.of("message",e.getMessage())); }
}
