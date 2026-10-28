package com.example.demo.infraestructura.adaptadores.salida.bd;

import com.example.demo.dominio.modelo.Vehiculo;
import com.example.demo.dominio.puerto.VehiculoRepositoryPort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class VehiculoRepositoryAdapter implements VehiculoRepositoryPort {

    private final SpringDataVehiculoRepository repository;

    public VehiculoRepositoryAdapter(SpringDataVehiculoRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<Vehiculo> findAll() {
        List<VehiculoEntity> entities = repository.findAll();
        return entities.stream().map(e -> new Vehiculo(
                e.getId(),
                e.getVin(),
                e.getColor(),
                e.getPrecio(),
                e.getEstadoVehiculo()
        )).collect(Collectors.toList());
    }

    @Override
    public Vehiculo findById(Long id) {
        VehiculoEntity entity = repository.findById(id).orElse(null);
        if (entity == null) {
            return null;
        }
        return new Vehiculo(
                entity.getId(),
                entity.getVin(),
                entity.getColor(),
                entity.getPrecio(),
                entity.getEstadoVehiculo()
        );
    }

    @Override
    public List<Vehiculo> obtenerPorColor(String color) {
        List<VehiculoEntity> entities;
        if (color == null || color.isEmpty()) {
            entities = repository.findAll();
        } else {
            entities = repository.findByColorIgnoreCase(color);
        }
        return entities.stream().map(e -> new Vehiculo(
                e.getId(),
                e.getVin(),
                e.getColor(),
                e.getPrecio(),
                e.getEstadoVehiculo()
        )).collect(Collectors.toList());
    }

    @Override
    public Vehiculo guardar(Vehiculo vehiculo) {
        VehiculoEntity entity = new VehiculoEntity();
        entity.setId(vehiculo.getId()); 
        entity.setVin(vehiculo.getVin());
        entity.setColor(vehiculo.getColor());
        entity.setPrecio(vehiculo.getPrecio());
        entity.setEstadoVehiculo(vehiculo.getEstadoVehiculo());
        
        VehiculoEntity saved = repository.save(entity);
        return new Vehiculo(saved.getId(), saved.getVin(), saved.getColor(), saved.getPrecio(), saved.getEstadoVehiculo());
    }
    
    @Override
    public void eliminar(Long id) {
        repository.deleteById(id);
    }
}