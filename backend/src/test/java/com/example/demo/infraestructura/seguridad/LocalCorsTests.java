package com.example.demo.infraestructura.seguridad;
import org.junit.jupiter.api.Test;
import com.example.demo.infraestructura.seguridad.SecurityConfig;
import org.springframework.mock.web.MockHttpServletRequest;
import static org.junit.jupiter.api.Assertions.*;
class LocalCorsTests {
 @Test void acceptsPrivateOriginsWithAndWithoutPortsAndRejectsUnrelatedHosts() {
  var request=new MockHttpServletRequest();request.setRequestURI("/api/auth/login");
  var config=new SecurityConfig().corsConfigurationSource().getCorsConfiguration(request);
  for(String origin:java.util.List.of("http://192.168.1.40:4200","http://192.168.1.40","http://10.0.0.3:4200","http://172.31.0.1:4200","http://miagencia.local:4200","http://api.miagencia.local:4200"))assertEquals(origin,config.checkOrigin(origin));
  assertNull(config.checkOrigin("https://unrelated.example.com"));
 }
}
