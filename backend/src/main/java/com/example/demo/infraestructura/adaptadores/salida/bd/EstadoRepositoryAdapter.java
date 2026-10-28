package com.example.demo.infraestructura.adaptadores.salida.bd;

import com.example.demo.dominio.modelo.Estado;
import com.example.demo.dominio.puerto.EstadoRepositoryPort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class EstadoRepositoryAdapter implements EstadoRepositoryPort {

    private final SpringDataEstadoRepository repository;

    public EstadoRepositoryAdapter(SpringDataEstadoRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<Estado> findAll() {
        List<EstadoEntity> entities = repository.findAll();
        return entities.stream().map(e -> new Estado(
                e.getId(),
                e.getNombre(),
                e.getPaisId()
        )).collect(Collectors.toList());
    }

    @Override
    public Estado findById(Long id) {
        EstadoEntity entity = repository.findById(id).orElse(null);
        if (entity == null) {
            return null;
        }
        return new Estado(entity.getId(), entity.getNombre(), entity.getPaisId());
    }

    @Override
    public Estado guardar(Estado estado) {
        EstadoEntity entity = new EstadoEntity();
        entity.setId(estado.getId());
        entity.setNombre(estado.getNombre());
        entity.setPaisId(estado.getPaisId());

        EstadoEntity saved = repository.save(entity);
        return new Estado(saved.getId(), saved.getNombre(), saved.getPaisId());
    }
}