package com.example.demo.dominio.puerto;

import com.example.demo.dominio.modelo.Usuario;
import java.util.List;

public interface UsuarioRepositoryPort {
    List<Usuario> findAll();
    Usuario findById(Long id);
    Usuario guardar(Usuario usuario);
}