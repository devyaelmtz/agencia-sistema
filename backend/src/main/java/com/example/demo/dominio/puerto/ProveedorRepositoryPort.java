package com.example.demo.dominio.puerto;

import com.example.demo.dominio.modelo.Proveedor;
import java.util.List;

public interface ProveedorRepositoryPort {
    List<Proveedor> findAll();
    Proveedor findById(Long id);
    Proveedor guardar(Proveedor proveedor);
}