package com.example.demo;
import com.example.demo.infraestructura.seguridad.TokenService;
import com.example.demo.dominio.modelo.Usuario;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import java.time.Instant;
import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
class JwtTests {
    @Autowired TokenService tokens;
    @Autowired JwtDecoder decoder;
    @Autowired JwtEncoder encoder;
    @Test void validatesSignatureAndIdentity() {
        String token = tokens.emitir(new Usuario(42L,"Test","test@example.invalid",null,null));
        assertEquals("42", decoder.decode(token).getSubject());
        String[] parts = token.split("\\.");
        parts[2] = (parts[2].startsWith("A") ? "B" : "A") + parts[2].substring(1);
        assertThrows(JwtException.class, () -> decoder.decode(String.join(".", parts)));
    }
    @Test void rejectsExpiredTokens() {
        var claims = JwtClaimsSet.builder().issuer("agencia-sistema").subject("42")
            .issuedAt(Instant.now().minusSeconds(7200)).expiresAt(Instant.now().minusSeconds(3600)).build();
        String token = encoder.encode(JwtEncoderParameters.from(JwsHeader.with(MacAlgorithm.HS256).build(), claims)).getTokenValue();
        assertThrows(JwtException.class, () -> decoder.decode(token));
    }
}
