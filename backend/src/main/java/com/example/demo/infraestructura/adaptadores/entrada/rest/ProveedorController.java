package com.example.demo.infraestructura.adaptadores.entrada.rest;

import com.example.demo.aplicacion.servicio.ProveedorUseCase;
import com.example.demo.dominio.modelo.Proveedor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/proveedores")
@CrossOrigin(origins = "*")
public class ProveedorController {

    private final ProveedorUseCase useCase;

    public ProveedorController(ProveedorUseCase useCase) {
        this.useCase = useCase;
    }

    // GET: Listar todos los proveedores
    @GetMapping
    public ResponseEntity<List<Proveedor>> listarProveedores() {
        List<Proveedor> lista = useCase.listarTodos();
        return ResponseEntity.ok(lista);
    }

    // GET: Buscar proveedor por ID
    @GetMapping("/{id}")
    public ResponseEntity<Proveedor> obtenerPorId(@PathVariable Long id) {
        Proveedor proveedor = useCase.buscarPorId(id);
        if (proveedor == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(proveedor);
    }

    // POST: Crear un nuevo proveedor
    @PostMapping
    public ResponseEntity<Proveedor> crearProveedor(@RequestBody Proveedor proveedor) {
        Proveedor nuevoProveedor = useCase.guardar(proveedor);
        return ResponseEntity.ok(nuevoProveedor);
    }

    // PUT: Actualizar un proveedor existente por ID
    @PutMapping("/{id}")
    public ResponseEntity<Proveedor> actualizarProveedor(@PathVariable Long id, @RequestBody Proveedor proveedor) {
        proveedor.setId(id);
        Proveedor proveedorActualizado = useCase.guardar(proveedor);
        return ResponseEntity.ok(proveedorActualizado);
    }
}