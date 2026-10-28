package com.example.demo.infraestructura.adaptadores.entrada.rest;

import com.example.demo.aplicacion.servicio.EmpleadoUseCase;
import com.example.demo.dominio.modelo.Empleado;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/empleados")
@CrossOrigin(origins = "*")
public class EmpleadoController {

    private final EmpleadoUseCase useCase;

    public EmpleadoController(EmpleadoUseCase useCase) {
        this.useCase = useCase;
    }

    // GET: Listar todos los empleados
    @GetMapping
    public ResponseEntity<List<Empleado>> listarEmpleados() {
        List<Empleado> lista = useCase.listarTodos();
        return ResponseEntity.ok(lista);
    }

    // GET: Buscar empleado por ID
    @GetMapping("/{id}")
    public ResponseEntity<Empleado> obtenerPorId(@PathVariable Long id) {
        Empleado empleado = useCase.buscarPorId(id);
        if (empleado == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(empleado);
    }

    // POST: Crear un nuevo empleado (asociando sucursalId y usuarioId previos)
    @PostMapping
    public ResponseEntity<Empleado> crearEmpleado(@RequestBody Empleado empleado) {
        Empleado nuevoEmpleado = useCase.guardar(empleado);
        return ResponseEntity.ok(nuevoEmpleado);
    }

    // PUT: Actualizar un empleado existente por ID
    @PutMapping("/{id}")
    public ResponseEntity<Empleado> actualizarEmpleado(@PathVariable Long id, @RequestBody Empleado empleado) {
        empleado.setId(id);
        Empleado empleadoActualizado = useCase.guardar(empleado);
        return ResponseEntity.ok(empleadoActualizado);
    }
}