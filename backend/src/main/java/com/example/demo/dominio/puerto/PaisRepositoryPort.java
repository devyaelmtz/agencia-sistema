package com.example.demo.dominio.puerto;

import com.example.demo.dominio.modelo.Pais;
import java.util.List;

public interface PaisRepositoryPort {
    List<Pais> findAll();
    Pais findById(Long id);
    Pais guardar(Pais pais);
}