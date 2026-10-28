package com.example.demo.aplicacion.servicio;
import com.example.demo.dominio.modelo.Usuario;
import com.example.demo.dominio.puerto.UsuarioRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import java.util.Locale;
@Service
public class AuthUseCase {
    private final UsuarioRepositoryPort usuarios;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    public AuthUseCase(UsuarioRepositoryPort usuarios) { this.usuarios = usuarios; }
    private String correo(String value) {
        if (value == null || value.trim().length() > 100 || !value.trim().matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$"))
            throw new IllegalArgumentException("Introduce un correo válido.");
        return value.trim().toLowerCase(Locale.ROOT);
    }
    public synchronized Usuario registrar(String nombre, String email, String telefono, String password) {
        String normalizado = correo(email);
        if (nombre == null || nombre.trim().length() < 2 || nombre.trim().length() > 50)
            throw new IllegalArgumentException("El nombre debe tener entre 2 y 50 caracteres.");
        if (telefono == null || !telefono.matches("^[0-9]{10}$"))
            throw new IllegalArgumentException("El teléfono debe tener exactamente 10 dígitos.");
        if (password == null || password.length() < 8 || password.length() > 64 || password.getBytes(java.nio.charset.StandardCharsets.UTF_8).length > 72)
            throw new IllegalArgumentException("La contraseña debe tener entre 8 y 64 caracteres (máximo 72 bytes).");
        if (usuarios.findByCorreo(normalizado) != null) throw new IllegalStateException("El correo ya está registrado.");
        Usuario u = new Usuario(null, nombre.trim(), normalizado, encoder.encode(password), null);
        u.setTelefono(telefono);
        return usuarios.guardar(u);
    }
    public Usuario login(String email, String password) {
        Usuario u = usuarios.findByCorreo(correo(email));
        if (u == null || password == null || !encoder.matches(password, u.getPassword()))
            throw new SecurityException("Correo o contraseña incorrectos.");
        return u;
    }
}
