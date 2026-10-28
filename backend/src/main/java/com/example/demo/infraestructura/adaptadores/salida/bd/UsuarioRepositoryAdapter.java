package com.example.demo.infraestructura.adaptadores.salida.bd;

import com.example.demo.dominio.modelo.Usuario;
import com.example.demo.dominio.puerto.UsuarioRepositoryPort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class UsuarioRepositoryAdapter implements UsuarioRepositoryPort {

    private final SpringDataUsuarioRepository repository;

    public UsuarioRepositoryAdapter(SpringDataUsuarioRepository repository) {
        this.repository = repository;
    }

    private Usuario map(UsuarioEntity e) {
        Usuario u = new Usuario(e.getId(), e.getNombreUsuario(), e.getCorreo(), e.getPassword(), e.getRolId());
        u.setTelefono(e.getTelefono());
        return u;
    }
    @Override
    public Usuario findByCorreo(String correo) { return repository.findByCorreoIgnoreCase(correo).map(this::map).orElse(null); }
    @Override
    public List<Usuario> findAll() {
        List<UsuarioEntity> entities = repository.findAll();
        return entities.stream().map(this::map).collect(Collectors.toList());
    }

    @Override
    public Usuario findById(Long id) {
        UsuarioEntity entity = repository.findById(id).orElse(null);
        if (entity == null) {
            return null;
        }
        return map(entity);
    }

    @Override
    public Usuario guardar(Usuario usuario) {
        UsuarioEntity entity = new UsuarioEntity();
        entity.setId(usuario.getId());
        entity.setNombreUsuario(usuario.getNombreUsuario());
        entity.setCorreo(usuario.getCorreo());
        entity.setPassword(usuario.getPassword());
        entity.setRolId(usuario.getRolId());
        entity.setTelefono(usuario.getTelefono());

        UsuarioEntity saved = repository.save(entity);
        return map(saved);
    }
}