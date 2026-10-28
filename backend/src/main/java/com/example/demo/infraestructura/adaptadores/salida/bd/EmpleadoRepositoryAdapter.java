package com.example.demo.infraestructura.adaptadores.salida.bd;

import com.example.demo.dominio.modelo.Empleado;
import com.example.demo.dominio.puerto.EmpleadoRepositoryPort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class EmpleadoRepositoryAdapter implements EmpleadoRepositoryPort {

    private final SpringDataEmpleadoRepository repository;

    public EmpleadoRepositoryAdapter(SpringDataEmpleadoRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<Empleado> findAll() {
        List<EmpleadoEntity> entities = repository.findAll();
        return entities.stream().map(e -> new Empleado(
                e.getId(),
                e.getNombre(),
                e.getApellido(),
                e.getPuesto(),
                e.getSucursalId(),
                e.getUsuarioId()
        )).collect(Collectors.toList());
    }

    @Override
    public Empleado findById(Long id) {
        EmpleadoEntity entity = repository.findById(id).orElse(null);
        if (entity == null) {
            return null;
        }
        return new Empleado(
                entity.getId(),
                entity.getNombre(),
                entity.getApellido(),
                entity.getPuesto(),
                entity.getSucursalId(),
                entity.getUsuarioId()
        );
    }

    @Override
    public Empleado guardar(Empleado empleado) {
        EmpleadoEntity entity = new EmpleadoEntity();
        entity.setId(empleado.getId());
        entity.setNombre(empleado.getNombre());
        entity.setApellido(empleado.getApellido());
        entity.setPuesto(empleado.getPuesto());
        entity.setSucursalId(empleado.getSucursalId());
        entity.setUsuarioId(empleado.getUsuarioId());

        EmpleadoEntity saved = repository.save(entity);
        return new Empleado(
                saved.getId(),
                saved.getNombre(),
                saved.getApellido(),
                saved.getPuesto(),
                saved.getSucursalId(),
                saved.getUsuarioId()
        );
    }
}