package com.example.demo;
import com.example.demo.aplicacion.servicio.AuthUseCase;
import com.example.demo.dominio.puerto.UsuarioRepositoryPort;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
@Transactional
class AuthUseCaseTests {
    @Autowired AuthUseCase auth;
    @Autowired UsuarioRepositoryPort usuarios;
    @Test void registerPersistsPhoneAndHashedPasswordAndAllowsLogin() {
        var u = auth.registrar("Prueba", "AUTH-test@example.invalid", "5512345678", "ClaveSegura123");
        var saved = usuarios.findById(u.getId());
        assertEquals("5512345678", saved.getTelefono());
        assertNotEquals("ClaveSegura123", saved.getPassword());
        assertEquals(u.getId(), auth.login("auth-TEST@example.invalid", "ClaveSegura123").getId());
        assertThrows(SecurityException.class, () -> auth.login("auth-test@example.invalid", "Incorrecta123"));
        assertThrows(IllegalStateException.class, () -> auth.registrar("Otra", "auth-test@example.invalid", "5512345678", "ClaveSegura123"));
    }
    @Test void rejectsInvalidAndMissingPhones() {
        for (String phone : new String[]{"", "123", "12345678901", "abcdefghij", "551234567 "}) {
            assertThrows(IllegalArgumentException.class, () -> auth.registrar("Prueba", "invalid@example.invalid", phone, "ClaveSegura123"));
        }
        assertThrows(IllegalArgumentException.class, () -> auth.registrar("Prueba", "invalid@example.invalid", null, "ClaveSegura123"));
    }
}
