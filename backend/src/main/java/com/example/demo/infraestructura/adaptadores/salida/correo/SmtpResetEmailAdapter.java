package com.example.demo.infraestructura.adaptadores.salida.correo;
import com.example.demo.dominio.puerto.ResetEmailPort;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.beans.factory.annotation.Value;
@org.springframework.stereotype.Component
public class SmtpResetEmailAdapter implements ResetEmailPort {
    private final JavaMailSender sender; private final String from; private final String url;
    public SmtpResetEmailAdapter(JavaMailSender sender,@Value("${app.mail.from}") String from,@Value("${app.frontend-url}") String url) { this.sender=sender;this.from=from;this.url=url; }
    public void enviar(String correo,String token) {
        var message=new SimpleMailMessage();message.setFrom(from);message.setTo(correo);message.setSubject("Recupera tu acceso a Mi Agencia");
        message.setText("Recibimos una solicitud de cambio de contraseña. El código caduca en 15 minutos y solo puede usarse una vez.\n\nCódigo: "+token+"\n\nAbre: "+url+"/reset-password?token="+token+"\n\nSi no lo solicitaste, ignora este correo.");
        sender.send(message);
    }
}
