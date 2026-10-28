package com.example.demo.dominio.puerto;

import com.example.demo.dominio.modelo.Cliente;
import java.util.List;

public interface ClienteRepositoryPort {
    List<Cliente> findAll();

    Cliente findById(Long id);

    Cliente guardar(Cliente cliente);

    void eliminar(Long id);

    Cliente login(String correo, String telefono);
}