package com.example.demo;
import com.example.demo.aplicacion.servicio.*;
import com.example.demo.dominio.modelo.*;
import com.example.demo.dominio.puerto.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;
import org.mockito.ArgumentCaptor;
import java.time.Instant;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest @Transactional
class RecoveryArcoTests {
    @Autowired AuthUseCase auth;
    @Autowired PasswordResetService reset;
    @Autowired PasswordResetPort tokens;
    @Autowired ArcoService arco;
    @MockitoBean ResetEmailPort mail;
    @Test void recoveryChangesPasswordAndConsumesToken() {
        auth.registrar("Test reset","reset-test@example.invalid","5512345678","Anterior123");
        reset.solicitar("RESET-test@example.invalid");
        var token=ArgumentCaptor.forClass(String.class);
        verify(mail).enviar(eq("reset-test@example.invalid"),token.capture());
        String code=token.getValue();
        var stored=tokens.bloquear(PasswordResetService.hash(code)).orElseThrow();
        assertNotEquals(code,stored.tokenHash());
        reset.reset(code,"NuevaClave123");
        assertNotNull(auth.login("reset-test@example.invalid","NuevaClave123"));
        assertThrows(SecurityException.class,()->auth.login("reset-test@example.invalid","Anterior123"));
        assertThrows(IllegalArgumentException.class,()->reset.reset(code,"OtraClave123"));
    }
    @Test void unknownEmailDoesNotSendMailAndInvalidTokensFail() {
        reset.solicitar("unknown-reset@example.invalid");
        verifyNoInteractions(mail);
        assertThrows(IllegalArgumentException.class,()->reset.reset("invalid","NuevaClave123"));
    }
    @Test void expiredTokensCannotChangePassword() {
        var u=auth.registrar("Test expiry","expiry@example.invalid","5512345678","Anterior123");
        String code="A".repeat(43);
        tokens.guardar(new PasswordResetToken(PasswordResetService.hash(code),u.getId(),Instant.now().minusSeconds(1)));
        assertThrows(IllegalArgumentException.class,()->reset.reset(code,"NuevaClave123"));
        assertNotNull(auth.login("expiry@example.invalid","Anterior123"));
    }
    @Test void newRecoveryInvalidatesPreviousCode() {
        auth.registrar("Test reissue","reissue@example.invalid","5512345678","Anterior123");
        reset.solicitar("reissue@example.invalid"); reset.solicitar("reissue@example.invalid");
        var code=ArgumentCaptor.forClass(String.class);
        verify(mail,times(2)).enviar(eq("reissue@example.invalid"),code.capture());
        assertThrows(IllegalArgumentException.class,()->reset.reset(code.getAllValues().get(0),"NuevaClave123"));
        reset.reset(code.getAllValues().get(1),"NuevaClave123");
    }
    @Test void arcoRequiresNoticeAndOnlyListsOwnerRequests() {
        var a=auth.registrar("ARCO uno","arco-one@example.invalid","5512345678","ClaveSegura123");
        var b=auth.registrar("ARCO dos","arco-two@example.invalid","5512345678","ClaveSegura123");
        assertThrows(IllegalArgumentException.class,()->arco.crear(a.getId(),ArcoRequest.Tipo.ACCESO,"Quiero consultar mis datos",null,false,ArcoService.POLICY_VERSION));
        assertThrows(IllegalArgumentException.class,()->arco.crear(a.getId(),ArcoRequest.Tipo.ACCESO,"Quiero consultar mis datos",null,true,"old"));
        var request=arco.crear(a.getId(),ArcoRequest.Tipo.ACCESO,"Quiero consultar mis datos",null,true,ArcoService.POLICY_VERSION);
        assertEquals("PENDIENTE",request.estado());assertTrue(request.avisoLeido());
        assertEquals(a.getId(),request.usuarioId());assertEquals(1,arco.listar(a.getId()).size());assertTrue(arco.listar(b.getId()).isEmpty());
    }
}
