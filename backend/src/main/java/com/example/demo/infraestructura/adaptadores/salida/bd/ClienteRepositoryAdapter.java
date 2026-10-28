package com.example.demo.infraestructura.adaptadores.salida.bd;

import com.example.demo.dominio.modelo.Cliente;
import com.example.demo.dominio.puerto.ClienteRepositoryPort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ClienteRepositoryAdapter implements ClienteRepositoryPort {

    private final SpringDataClienteRepository repository;

    public ClienteRepositoryAdapter(SpringDataClienteRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<Cliente> findAll() {
        List<ClienteEntity> entities = repository.findAll();
        return entities.stream().map(e -> new Cliente(
                e.getId(),
                e.getNombre(),
                e.getApellido(),
                e.getTelefono(),
                e.getCorreo(),
                e.getDireccion(),
                e.getRecibirPublicidad() // <-- NUEVO CAMPO MAPEADO
        )).collect(Collectors.toList());
    }

    @Override
    public Cliente findById(Long id) {
        ClienteEntity entity = repository.findById(id).orElse(null);
        if (entity == null) {
            return null;
        }
        return new Cliente(
                entity.getId(),
                entity.getNombre(),
                entity.getApellido(),
                entity.getTelefono(),
                entity.getCorreo(),
                entity.getDireccion(),
                entity.getRecibirPublicidad() // <-- NUEVO CAMPO MAPEADO
        );
    }

    @Override
    public Cliente guardar(Cliente cliente) {
        ClienteEntity entity = new ClienteEntity();
        entity.setId(cliente.getId());
        entity.setNombre(cliente.getNombre());
        entity.setApellido(cliente.getApellido());
        entity.setTelefono(cliente.getTelefono());
        entity.setCorreo(cliente.getCorreo());
        entity.setDireccion(cliente.getDireccion());
        entity.setRecibirPublicidad(cliente.getRecibirPublicidad()); // <-- NUEVO CAMPO ASIGNADO

        ClienteEntity saved = repository.save(entity);
        return new Cliente(
                saved.getId(),
                saved.getNombre(),
                saved.getApellido(),
                saved.getTelefono(),
                saved.getCorreo(),
                saved.getDireccion(),
                saved.getRecibirPublicidad() // <-- NUEVO CAMPO MAPEADO
        );
    }

    // --- NUEVO MÉTODO PARA EL DERECHO DE CANCELACIÓN ---
    @Override
    public void eliminar(Long id) {
        repository.deleteById(id);
    }

    @Override
    public Cliente login(String correo, String telefono) {
        ClienteEntity entity = repository.findByCorreoAndTelefono(correo, telefono);
        if (entity == null)
            return null;

        return new Cliente(
                entity.getId(), entity.getNombre(), entity.getApellido(),
                entity.getTelefono(), entity.getCorreo(), entity.getDireccion(),
                entity.getRecibirPublicidad());
    }
}