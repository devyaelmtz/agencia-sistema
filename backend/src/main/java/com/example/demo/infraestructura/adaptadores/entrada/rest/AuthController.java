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
    private final com.example.demo.infraestructura.seguridad.TokenService tokens;
    private final com.example.demo.dominio.puerto.UsuarioRepositoryPort usuarios;
    public AuthController(AuthUseCase auth, com.example.demo.infraestructura.seguridad.TokenService tokens, com.example.demo.dominio.puerto.UsuarioRepositoryPort usuarios) { this.auth = auth; this.tokens = tokens; this.usuarios = usuarios; }
    public record Sesion(String accessToken, String tokenType, long expiresIn, Perfil usuario) {}
    @GetMapping("/me")
    public Perfil me(@org.springframework.security.core.annotation.AuthenticationPrincipal org.springframework.security.oauth2.jwt.Jwt jwt) {
        Usuario u = usuarios.findById(Long.valueOf(jwt.getSubject()));
        if (u == null) throw new SecurityException("El usuario ya no existe.");
        return Perfil.from(u);
    }
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
    public Sesion login(@RequestBody Login r) {
        Usuario u = auth.login(r.correo(),r.password());
        return new Sesion(tokens.emitir(u), "Bearer", 3600, Perfil.from(u));
    }
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> invalid(IllegalArgumentException e) { return ResponseEntity.badRequest().body(Map.of("message",e.getMessage())); }
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<?> duplicate(IllegalStateException e) { return ResponseEntity.status(409).body(Map.of("message",e.getMessage())); }
    @ExceptionHandler(org.springframework.dao.DataIntegrityViolationException.class)
    public ResponseEntity<?> conflict() { return ResponseEntity.status(409).body(Map.of("message","El correo ya está registrado.")); }
    @ExceptionHandler(SecurityException.class)
    public ResponseEntity<?> unauthorized(SecurityException e) { return ResponseEntity.status(401).body(Map.of("message",e.getMessage())); }
}
