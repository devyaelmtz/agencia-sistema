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

    @Override
    public List<Usuario> findAll() {
        List<UsuarioEntity> entities = repository.findAll();
        return entities.stream().map(e -> new Usuario(
                e.getId(),
                e.getNombreUsuario(),
                e.getCorreo(),
                e.getPassword(),
                e.getRolId()
        )).collect(Collectors.toList());
    }

    @Override
    public Usuario findById(Long id) {
        UsuarioEntity entity = repository.findById(id).orElse(null);
        if (entity == null) {
            return null;
        }
        return new Usuario(entity.getId(), entity.getNombreUsuario(), entity.getCorreo(), entity.getPassword(), entity.getRolId());
    }

    @Override
    public Usuario guardar(Usuario usuario) {
        UsuarioEntity entity = new UsuarioEntity();
        entity.setId(usuario.getId());
        entity.setNombreUsuario(usuario.getNombreUsuario());
        entity.setCorreo(usuario.getCorreo());
        entity.setPassword(usuario.getPassword());
        entity.setRolId(usuario.getRolId());

        UsuarioEntity saved = repository.save(entity);
        return new Usuario(saved.getId(), saved.getNombreUsuario(), saved.getCorreo(), saved.getPassword(), saved.getRolId());
    }
}