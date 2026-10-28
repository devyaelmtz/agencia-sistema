package com.example.demo.infraestructura.adaptadores.entrada.rest;

import com.example.demo.aplicacion.servicio.RolUseCase;
import com.example.demo.dominio.modelo.Rol;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/roles")
@CrossOrigin(origins = "*")
public class RolController {

    private final RolUseCase useCase;

    public RolController(RolUseCase useCase) {
        this.useCase = useCase;
    }

    // GET: Listar todos los roles
    @GetMapping
    public ResponseEntity<List<Rol>> listarRoles() {
        List<Rol> lista = useCase.listarTodos();
        return ResponseEntity.ok(lista);
    }

    // GET: Buscar rol por ID
    @GetMapping("/{id}")
    public ResponseEntity<Rol> obtenerPorId(@PathVariable Long id) {
        Rol rol = useCase.buscarPorId(id);
        if (rol == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(rol);
    }

    // POST: Crear un nuevo rol
    @PostMapping
    public ResponseEntity<Rol> crearRol(@RequestBody Rol rol) {
        Rol nuevoRol = useCase.guardar(rol);
        return ResponseEntity.ok(nuevoRol);
    }

    // PUT: Actualizar un rol existente por ID
    @PutMapping("/{id}")
    public ResponseEntity<Rol> actualizarRol(@PathVariable Long id, @RequestBody Rol rol) {
        rol.setId(id);
        Rol rolActualizado = useCase.guardar(rol);
        return ResponseEntity.ok(rolActualizado);
    }
}