package com.example.demo.aplicacion.servicio;

import com.example.demo.dominio.modelo.Pais;
import com.example.demo.dominio.puerto.PaisRepositoryPort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PaisUseCase {

    private final PaisRepositoryPort paisRepositoryPort;

    public PaisUseCase(PaisRepositoryPort paisRepositoryPort) {
        this.paisRepositoryPort = paisRepositoryPort;
    }

    public List<Pais> listarTodos() {
        return paisRepositoryPort.findAll();
    }

    public Pais buscarPorId(Long id) {
        return paisRepositoryPort.findById(id);
    }

    public Pais guardar(Pais pais) {
        return paisRepositoryPort.guardar(pais);
    }
}