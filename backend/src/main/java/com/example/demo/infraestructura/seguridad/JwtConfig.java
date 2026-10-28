package com.example.demo.infraestructura.seguridad;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;
import java.nio.charset.StandardCharsets;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import com.nimbusds.jose.jwk.source.ImmutableSecret;
@Configuration
public class JwtConfig {
    @Bean SecretKey jwtKey(@Value("${JWT_SECRET:}") String secret) {
        byte[] bytes;
        if (secret.isEmpty()) {
            bytes = new byte[32]; new SecureRandom().nextBytes(bytes);
            org.slf4j.LoggerFactory.getLogger(JwtConfig.class).warn("JWT_SECRET no configurado: clave temporal de desarrollo; reiniciar invalida los tokens.");
        } else {
            bytes = secret.getBytes(StandardCharsets.UTF_8);
            if (bytes.length < 32) throw new IllegalArgumentException("JWT_SECRET requiere al menos 32 bytes.");
        }
        return new SecretKeySpec(bytes,"HmacSHA256");
    }
    @Bean JwtEncoder jwtEncoder(SecretKey key) { return new NimbusJwtEncoder(new ImmutableSecret<>(key)); }
    @Bean JwtDecoder jwtDecoder(SecretKey key) {
        NimbusJwtDecoder decoder = NimbusJwtDecoder.withSecretKey(key).macAlgorithm(MacAlgorithm.HS256).build();
        decoder.setJwtValidator(JwtValidators.createDefaultWithIssuer("agencia-sistema"));
        return decoder;
    }
}
