package com.example.demo;

import static org.junit.jupiter.api.Assertions.*;

import com.example.demo.infraestructura.seguridad.TokenService;
import java.time.Instant;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;

@SpringBootTest
@org.springframework.transaction.annotation.Transactional
class JwtTests {
  @Autowired TokenService tokens;
  @Autowired com.example.demo.aplicacion.servicio.AuthUseCase auth;
  @Autowired JwtDecoder decoder;
  @Autowired JwtEncoder encoder;

  @Test
  void validatesSignatureAndIdentity() {
    String token =
        tokens.emitir(
            auth.registrar(
                "JWT test", "jwt-identity@example.invalid", "5551234567", "ClaveSegura123"));
    assertNotNull(decoder.decode(token).getSubject());
    String[] parts = token.split("\\.");
    parts[2] = (parts[2].startsWith("A") ? "B" : "A") + parts[2].substring(1);
    assertThrows(JwtException.class, () -> decoder.decode(String.join(".", parts)));
  }

  @Test
  void rejectsExpiredTokens() {
    var claims =
        JwtClaimsSet.builder()
            .issuer("agencia-sistema")
            .subject("42")
            .issuedAt(Instant.now().minusSeconds(7200))
            .expiresAt(Instant.now().minusSeconds(3600))
            .build();
    String token =
        encoder
            .encode(JwtEncoderParameters.from(JwsHeader.with(MacAlgorithm.HS256).build(), claims))
            .getTokenValue();
    assertThrows(JwtException.class, () -> decoder.decode(token));
  }
}
