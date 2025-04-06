package com.svit.server_vitals.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class MailService {

    @Autowired
    private JavaMailSender mailSender; 
    public void enviarCorreoAlerta(String destinatario, String asunto, String mensaje) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(destinatario);
        message.setSubject(asunto);
        message.setText(mensaje);
        message.setFrom("alertasdaw@gmail.com");

        try {
            mailSender.send(message);
            System.out.println("Correo enviado con éxito.");
        } catch (MailException e) {
            System.out.println("Error al enviar el correo: " + e.getMessage());
        }
    }
}

