package com.example.demo.infraestructura.adaptadores.salida.bd;

import com.example.demo.dominio.modelo.Proveedor;
import com.example.demo.dominio.puerto.ProveedorRepositoryPort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ProveedorRepositoryAdapter implements ProveedorRepositoryPort {

    private final SpringDataProveedorRepository repository;

    public ProveedorRepositoryAdapter(SpringDataProveedorRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<Proveedor> findAll() {
        List<ProveedorEntity> entities = repository.findAll();
        return entities.stream().map(e -> new Proveedor(
                e.getId(),
                e.getNombreEmpresa(),
                e.getContacto(),
                e.getTelefono(),
                e.getCorreo()
        )).collect(Collectors.toList());
    }

    @Override
    public Proveedor findById(Long id) {
        ProveedorEntity entity = repository.findById(id).orElse(null);
        if (entity == null) {
            return null;
        }
        return new Proveedor(
                entity.getId(),
                entity.getNombreEmpresa(),
                entity.getContacto(),
                entity.getTelefono(),
                entity.getCorreo()
        );
    }

    @Override
    public Proveedor guardar(Proveedor proveedor) {
        ProveedorEntity entity = new ProveedorEntity();
        entity.setId(proveedor.getId());
        entity.setNombreEmpresa(proveedor.getNombreEmpresa());
        entity.setContacto(proveedor.getContacto());
        entity.setTelefono(proveedor.getTelefono());
        entity.setCorreo(proveedor.getCorreo());

        ProveedorEntity saved = repository.save(entity);
        return new Proveedor(
                saved.getId(),
                saved.getNombreEmpresa(),
                saved.getContacto(),
                saved.getTelefono(),
                saved.getCorreo()
        );
    }
}