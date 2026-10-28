package com.example.demo.aplicacion.servicio;

import com.example.demo.dominio.modelo.Marca;
import com.example.demo.dominio.puerto.MarcaRepositoryPort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MarcaUseCase {

    private final MarcaRepositoryPort marcaRepositoryPort;

    public MarcaUseCase(MarcaRepositoryPort marcaRepositoryPort) {
        this.marcaRepositoryPort = marcaRepositoryPort;
    }

    public List<Marca> listarTodos() {
        return marcaRepositoryPort.findAll();
    }

    public Marca buscarPorId(Long id) {
        return marcaRepositoryPort.findById(id);
    }

    public Marca guardar(Marca marca) {
        return marcaRepositoryPort.guardar(marca);
    }
}