package com.example.demo.aplicacion.servicio;

import com.example.demo.dominio.modelo.Usuario;
import com.example.demo.dominio.puerto.UsuarioRepositoryPort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsuarioUseCase {

    private final UsuarioRepositoryPort usuarioRepositoryPort;

    public UsuarioUseCase(UsuarioRepositoryPort usuarioRepositoryPort) {
        this.usuarioRepositoryPort = usuarioRepositoryPort;
    }

    public List<Usuario> listarTodos() {
        return usuarioRepositoryPort.findAll();
    }

    public Usuario buscarPorId(Long id) {
        return usuarioRepositoryPort.findById(id);
    }

    public Usuario guardar(Usuario usuario) {
        return usuarioRepositoryPort.guardar(usuario);
    }
}