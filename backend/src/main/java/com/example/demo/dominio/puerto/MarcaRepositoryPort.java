package com.example.demo.dominio.puerto;

import com.example.demo.dominio.modelo.Marca;
import java.util.List;

public interface MarcaRepositoryPort {
    List<Marca> findAll();
    Marca findById(Long id);
    Marca guardar(Marca marca);
}