package com.example.demo.infraestructura.adaptadores.salida.bd;

import com.example.demo.dominio.modelo.Marca;
import com.example.demo.dominio.puerto.MarcaRepositoryPort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class MarcaRepositoryAdapter implements MarcaRepositoryPort {

    private final SpringDataMarcaRepository repository;

    public MarcaRepositoryAdapter(SpringDataMarcaRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<Marca> findAll() {
        List<MarcaEntity> entities = repository.findAll();
        return entities.stream().map(e -> new Marca(
                e.getId(),
                e.getNombre(),
                e.getPaisOrigen()
        )).collect(Collectors.toList());
    }

    @Override
    public Marca findById(Long id) {
        MarcaEntity entity = repository.findById(id).orElse(null);
        if (entity == null) {
            return null;
        }
        return new Marca(entity.getId(), entity.getNombre(), entity.getPaisOrigen());
    }

    @Override
    public Marca guardar(Marca marca) {
        MarcaEntity entity = new MarcaEntity();
        entity.setId(marca.getId());
        entity.setNombre(marca.getNombre());
        entity.setPaisOrigen(marca.getPaisOrigen());

        MarcaEntity saved = repository.save(entity);
        return new Marca(saved.getId(), saved.getNombre(), saved.getPaisOrigen());
    }
}