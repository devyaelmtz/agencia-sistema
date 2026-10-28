package com.example.demo;
import com.example.demo.infraestructura.adaptadores.salida.correo.SmtpResetEmailAdapter;
import org.junit.jupiter.api.Test;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.mockito.ArgumentCaptor;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
class SmtpResetEmailTests {
    @Test void buildsMailWithCodeAndConfiguredLink() {
        var sender=mock(JavaMailSender.class);
        new SmtpResetEmailAdapter(sender,"sender@example.invalid","http://miagencia.local:4200").enviar("receiver@example.invalid","code");
        var message=ArgumentCaptor.forClass(SimpleMailMessage.class);verify(sender).send(message.capture());
        assertEquals("receiver@example.invalid",message.getValue().getTo()[0]);
        assertTrue(message.getValue().getText().contains("/reset-password?token=code"));
        assertEquals("sender@example.invalid",message.getValue().getFrom());
    }
}
