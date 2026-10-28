package com.example.demo.infraestructura.adaptadores.salida.bd;

import com.example.demo.dominio.modelo.Sucursal;
import com.example.demo.dominio.puerto.SucursalRepositoryPort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class SucursalRepositoryAdapter implements SucursalRepositoryPort {

    private final SpringDataSucursalRepository repository;

    public SucursalRepositoryAdapter(SpringDataSucursalRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<Sucursal> findAll() {
        List<SucursalEntity> entities = repository.findAll();
        return entities.stream().map(e -> new Sucursal(
                e.getId(),
                e.getNombre(),
                e.getDireccion(),
                e.getEstadoId()
        )).collect(Collectors.toList());
    }

    @Override
    public Sucursal findById(Long id) {
        SucursalEntity entity = repository.findById(id).orElse(null);
        if (entity == null) {
            return null;
        }
        return new Sucursal(entity.getId(), entity.getNombre(), entity.getDireccion(), entity.getEstadoId());
    }

    @Override
    public Sucursal guardar(Sucursal sucursal) {
        SucursalEntity entity = new SucursalEntity();
        entity.setId(sucursal.getId());
        entity.setNombre(sucursal.getNombre());
        entity.setDireccion(sucursal.getDireccion());
        entity.setEstadoId(sucursal.getEstadoId());

        SucursalEntity saved = repository.save(entity);
        return new Sucursal(saved.getId(), saved.getNombre(), saved.getDireccion(), saved.getEstadoId());
    }
}