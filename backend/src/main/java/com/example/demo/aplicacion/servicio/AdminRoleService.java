package com.example.demo.aplicacion.servicio;
import com.example.demo.dominio.puerto.UsuarioRepositoryPort;
import org.springframework.stereotype.Service;
@Service
public class AdminRoleService {
    private final UsuarioRepositoryPort usuarios;
    public AdminRoleService(UsuarioRepositoryPort usuarios) { this.usuarios = usuarios; }
    public void promover(Long id) {
        var u = usuarios.findById(id);
        if (u == null) throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.NOT_FOUND);
        u.setRol("ROLE_ADMIN"); usuarios.guardar(u);
    }
    public void bootstrap(String correo) {
        var u = usuarios.findByCorreo(correo.trim());
        if (u == null) throw new IllegalStateException("Registra la cuenta bootstrap antes de configurar ADMIN_BOOTSTRAP_EMAIL.");
        promover(u.getId());
    }
}
