package com.example.demo.aplicacion.servicio;

import com.example.demo.dominio.modelo.Cliente;
import com.example.demo.dominio.puerto.ClienteRepositoryPort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClienteUseCase {

    private final ClienteRepositoryPort clienteRepositoryPort;

    public ClienteUseCase(ClienteRepositoryPort clienteRepositoryPort) {
        this.clienteRepositoryPort = clienteRepositoryPort;
    }

    public List<Cliente> listarTodos() {
        return clienteRepositoryPort.findAll();
    }

    public Cliente buscarPorId(Long id) {
        return clienteRepositoryPort.findById(id);
    }

    public Cliente guardar(Cliente cliente) {
        return clienteRepositoryPort.guardar(cliente);
    }

    public void eliminar(Long id) {
        clienteRepositoryPort.eliminar(id);
    }

    public Cliente login(String correo, String telefono) {
        return clienteRepositoryPort.login(correo, telefono);
    }
}