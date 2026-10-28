package com.example.demo.dominio.puerto;
import com.example.demo.dominio.modelo.ArcoRequest;
public interface ArcoRepositoryPort { ArcoRequest guardar(ArcoRequest r); java.util.List<ArcoRequest> listar(Long usuarioId); }
