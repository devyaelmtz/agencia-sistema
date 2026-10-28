package com.example.demo.infraestructura.adaptadores.salida.bd;

import com.example.demo.dominio.modelo.TipoVehiculo;
import com.example.demo.dominio.puerto.TipoVehiculoRepositoryPort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class TipoVehiculoRepositoryAdapter implements TipoVehiculoRepositoryPort {

    private final SpringDataTipoVehiculoRepository repository;

    public TipoVehiculoRepositoryAdapter(SpringDataTipoVehiculoRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<TipoVehiculo> findAll() {
        List<TipoVehiculoEntity> entities = repository.findAll();
        return entities.stream().map(e -> new TipoVehiculo(
                e.getId(),
                e.getCategoria(),
                e.getDescripcion()
        )).collect(Collectors.toList());
    }

    @Override
    public TipoVehiculo findById(Long id) {
        TipoVehiculoEntity entity = repository.findById(id).orElse(null);
        if (entity == null) {
            return null;
        }
        return new TipoVehiculo(entity.getId(), entity.getCategoria(), entity.getDescripcion());
    }

    @Override
    public TipoVehiculo guardar(TipoVehiculo tipoVehiculo) {
        TipoVehiculoEntity entity = new TipoVehiculoEntity();
        entity.setId(tipoVehiculo.getId());
        entity.setCategoria(tipoVehiculo.getCategoria());
        entity.setDescripcion(tipoVehiculo.getDescripcion());

        TipoVehiculoEntity saved = repository.save(entity);
        return new TipoVehiculo(saved.getId(), saved.getCategoria(), saved.getDescripcion());
    }
}