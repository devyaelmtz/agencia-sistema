package com.example.demo.infraestructura.adaptadores.salida.bd;

import com.example.demo.dominio.modelo.Modelo;
import com.example.demo.dominio.puerto.ModeloRepositoryPort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ModeloRepositoryAdapter implements ModeloRepositoryPort {

    private final SpringDataModeloRepository repository;

    public ModeloRepositoryAdapter(SpringDataModeloRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<Modelo> findAll() {
        List<ModeloEntity> entities = repository.findAll();
        return entities.stream().map(e -> new Modelo(
                e.getId(),
                e.getNombre(),
                e.getAnio(),
                e.getMarcaId(),
                e.getTipoVehiculoId()
        )).collect(Collectors.toList());
    }

    @Override
    public Modelo findById(Long id) {
        ModeloEntity entity = repository.findById(id).orElse(null);
        if (entity == null) {
            return null;
        }
        return new Modelo(
                entity.getId(),
                entity.getNombre(),
                entity.getAnio(),
                entity.getMarcaId(),
                entity.getTipoVehiculoId()
        );
    }

    @Override
    public Modelo guardar(Modelo modelo) {
        ModeloEntity entity = new ModeloEntity();
        entity.setId(modelo.getId());
        entity.setNombre(modelo.getNombre());
        entity.setAnio(modelo.getAnio());
        entity.setMarcaId(modelo.getMarcaId());
        entity.setTipoVehiculoId(modelo.getTipoVehiculoId());

        ModeloEntity saved = repository.save(entity);
        return new Modelo(
                saved.getId(),
                saved.getNombre(),
                saved.getAnio(),
                saved.getMarcaId(),
                saved.getTipoVehiculoId()
        );
    }
}