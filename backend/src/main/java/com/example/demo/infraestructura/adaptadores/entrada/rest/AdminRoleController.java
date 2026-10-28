package com.example.demo.infraestructura.adaptadores.entrada.rest;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/api/admin/users")
public class AdminRoleController {
    private final com.example.demo.aplicacion.servicio.AdminRoleService roles;
    public AdminRoleController(com.example.demo.aplicacion.servicio.AdminRoleService roles) { this.roles = roles; }
    @PutMapping("/{id}/admin") @ResponseStatus(org.springframework.http.HttpStatus.NO_CONTENT)
    public void promover(@PathVariable Long id) { roles.promover(id); }
}
