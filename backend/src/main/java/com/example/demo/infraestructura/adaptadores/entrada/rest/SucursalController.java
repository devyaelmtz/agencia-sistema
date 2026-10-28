package com.example.demo.infraestructura.adaptadores.entrada.rest;

import com.example.demo.aplicacion.servicio.SucursalUseCase;
import com.example.demo.dominio.modelo.Sucursal;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sucursales")
@CrossOrigin(origins = "*")
public class SucursalController {

    private final SucursalUseCase useCase;

    public SucursalController(SucursalUseCase useCase) {
        this.useCase = useCase;
    }

    // GET: Listar todas las sucursales
    @GetMapping
    public ResponseEntity<List<Sucursal>> listarSucursales() {
        List<Sucursal> lista = useCase.listarTodos();
        return ResponseEntity.ok(lista);
    }

    // GET: Buscar sucursal por ID
    @GetMapping("/{id}")
    public ResponseEntity<Sucursal> obtenerPorId(@PathVariable Long id) {
        Sucursal sucursal = useCase.buscarPorId(id);
        if (sucursal == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(sucursal);
    }

    // POST: Crear una nueva sucursal (relacionada con un estado existente por ID)
    @PostMapping
    public ResponseEntity<Sucursal> crearSucursal(@RequestBody Sucursal sucursal) {
        Sucursal nuevaSucursal = useCase.guardar(sucursal);
        return ResponseEntity.ok(nuevaSucursal);
    }

    // PUT: Actualizar una sucursal existente por ID
    @PutMapping("/{id}")
    public ResponseEntity<Sucursal> actualizarSucursal(@PathVariable Long id, @RequestBody Sucursal sucursal) {
        sucursal.setId(id);
        Sucursal sucursalActualizada = useCase.guardar(sucursal);
        return ResponseEntity.ok(sucursalActualizada);
    }
}