package com.example.demo.dominio.puerto;

import com.example.demo.dominio.modelo.Modelo;
import java.util.List;

public interface ModeloRepositoryPort {
    List<Modelo> findAll();
    Modelo findById(Long id);
    Modelo guardar(Modelo modelo);
}