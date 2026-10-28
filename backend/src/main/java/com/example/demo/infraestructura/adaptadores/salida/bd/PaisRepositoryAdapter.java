package com.example.demo.infraestructura.adaptadores.salida.bd;

import com.example.demo.dominio.modelo.Pais;
import com.example.demo.dominio.puerto.PaisRepositoryPort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class PaisRepositoryAdapter implements PaisRepositoryPort {

    private final SpringDataPaisRepository repository;

    public PaisRepositoryAdapter(SpringDataPaisRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<Pais> findAll() {
        List<PaisEntity> entities = repository.findAll();
        return entities.stream().map(e -> new Pais(
                e.getId(),
                e.getNombre()
        )).collect(Collectors.toList());
    }

    @Override
    public Pais findById(Long id) {
        PaisEntity entity = repository.findById(id).orElse(null);
        if (entity == null) {
            return null;
        }
        return new Pais(entity.getId(), entity.getNombre());
    }

    @Override
    public Pais guardar(Pais pais) {
        PaisEntity entity = new PaisEntity();
        entity.setId(pais.getId());
        entity.setNombre(pais.getNombre());

        PaisEntity saved = repository.save(entity);
        return new Pais(saved.getId(), saved.getNombre());
    }
}