package com.example.demo.infraestructura.adaptadores.salida.bd;

import com.example.demo.dominio.modelo.Rol;
import com.example.demo.dominio.puerto.RolRepositoryPort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class RolRepositoryAdapter implements RolRepositoryPort {

    private final SpringDataRolRepository repository;

    public RolRepositoryAdapter(SpringDataRolRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<Rol> findAll() {
        List<RolEntity> entities = repository.findAll();
        return entities.stream().map(e -> new Rol(
                e.getId(),
                e.getNombreRol(),
                e.getDescripcion()
        )).collect(Collectors.toList());
    }

    @Override
    public Rol findById(Long id) {
        RolEntity entity = repository.findById(id).orElse(null);
        if (entity == null) {
            return null;
        }
        return new Rol(entity.getId(), entity.getNombreRol(), entity.getDescripcion());
    }

    @Override
    public Rol guardar(Rol rol) {
        RolEntity entity = new RolEntity();
        entity.setId(rol.getId());
        entity.setNombreRol(rol.getNombreRol());
        entity.setDescripcion(rol.getDescripcion());

        RolEntity saved = repository.save(entity);
        return new Rol(saved.getId(), saved.getNombreRol(), saved.getDescripcion());
    }
}