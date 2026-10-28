package com.example.demo.dominio.puerto;

import com.example.demo.dominio.modelo.Rol;
import java.util.List;

public interface RolRepositoryPort {
    List<Rol> findAll();
    Rol findById(Long id);
    Rol guardar(Rol rol);
}