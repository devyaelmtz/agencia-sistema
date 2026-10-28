package com.example.demo.infraestructura.adaptadores.salida.bd;

import jakarta.persistence.*;

@Entity
@Table(name = "usuarios")
public class UsuarioEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nombre_usuario", length = 50)
    private String nombreUsuario;

    @Column(length = 100, unique = true)
    private String correo;

    @Column(length = 255)
    private String password;

    @Column(name = "rol_id")
    private Long rolId;

    private String rol = "ROLE_USER";
    public String getRol() { return rol == null ? "ROLE_USER" : rol; }
    public void setRol(String rol) { this.rol = rol; }
    private String telefono;
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public UsuarioEntity() {}

    public UsuarioEntity(Long id, String nombreUsuario, String correo, String password, Long rolId) {
        this.id = id;
        this.nombreUsuario = nombreUsuario;
        this.correo = correo;
        this.password = password;
        this.rolId = rolId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Long getRolId() {
        return rolId;
    }

    public void setRolId(Long rolId) {
        this.rolId = rolId;
    }
}