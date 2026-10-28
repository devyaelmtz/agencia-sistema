package com.example.demo.dominio.puerto;

import com.example.demo.dominio.modelo.Estado;
import java.util.List;

public interface EstadoRepositoryPort {
    List<Estado> findAll();
    Estado findById(Long id);
    Estado guardar(Estado estado);
}