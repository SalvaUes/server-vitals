package com.svit.server_vitals.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import com.svit.server_vitals.model.LogLevel;


@Service
public class MailService {

    private static final Logger log = LoggerFactory.getLogger(MailService.class);

    @Autowired
    private JavaMailSender mailSender;

    
    @Autowired
    private EventLogService eventLogService;

    @Value("${spring.mail.username}")
    private String mailUsername;

    public void enviarCorreoAlerta(String destinatario, String asunto, String mensaje) {
        log.info("Preparando correo para: {}, Asunto: {}", destinatario, asunto);
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(destinatario);
        message.setSubject(asunto);
        message.setText(mensaje);
        message.setFrom(mailUsername);
        log.info("Remitente establecido como: {}", mailUsername);

        try {
            
            log.info(">>> EJECUTANDO mailSender.send() AHORA MISMO...");
            
            mailSender.send(message);
            log.info("Correo enviado con éxito (según JavaMailSender) a: {}", destinatario);

            eventLogService.log(LogLevel.INFO, "Correo enviado a " + destinatario,
                                      "Asunto: " + asunto + ", Mensaje: " + mensaje,
                                      "MailService"); 
        } catch (MailException e) {
            log.error("Error al enviar el correo a {}: {}", destinatario, e.getMessage());
        } catch (Exception e) {
             log.error("Error inesperado al intentar enviar correo a {}: {}", destinatario, e.getMessage(), e);
        }
    }
}
