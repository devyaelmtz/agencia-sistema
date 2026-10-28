package com.example.demo.infraestructura.adaptadores.entrada.rest;

import com.example.demo.aplicacion.servicio.ClienteUseCase;
import com.example.demo.dominio.modelo.Cliente;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clientes")
@CrossOrigin(origins = "*", methods = { RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT,
        RequestMethod.DELETE })
public class ClienteController {

    private final ClienteUseCase useCase;

    public ClienteController(ClienteUseCase useCase) {
        this.useCase = useCase;
    }

    // GET: Listar todos los clientes
    @GetMapping
    public ResponseEntity<List<Cliente>> listarClientes() {
        List<Cliente> lista = useCase.listarTodos();
        return ResponseEntity.ok(lista);
    }

    // GET: Buscar cliente por ID
    @GetMapping("/{id}")
    public ResponseEntity<Cliente> obtenerPorId(@PathVariable Long id) {
        Cliente cliente = useCase.buscarPorId(id);
        if (cliente == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(cliente);
    }

    // POST: Crear un nuevo cliente
    @PostMapping
    public ResponseEntity<Cliente> crearCliente(@RequestBody Cliente cliente) {
        Cliente nuevoCliente = useCase.guardar(cliente);
        return ResponseEntity.ok(nuevoCliente);
    }

    // PUT: Actualizar un cliente existente por ID
    @PutMapping("/{id}")
    public ResponseEntity<Cliente> actualizarCliente(@PathVariable Long id, @RequestBody Cliente cliente) {
        cliente.setId(id);
        Cliente clienteActualizado = useCase.guardar(cliente);
        return ResponseEntity.ok(clienteActualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarCliente(@PathVariable Long id) {
        useCase.eliminar(id); // O clienteUseCase.eliminar(id); dependiendo de cómo llamaste a tu variable
                              // arriba
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/login")
    public org.springframework.http.ResponseEntity<Cliente> iniciarSesion(
            @RequestBody java.util.Map<String, String> credenciales) {
        Cliente cliente = useCase.login(credenciales.get("correo"), credenciales.get("telefono"));
        if (cliente != null) {
            return org.springframework.http.ResponseEntity.ok(cliente);
        }
        return org.springframework.http.ResponseEntity.status(org.springframework.http.HttpStatus.UNAUTHORIZED).build();
    }
}