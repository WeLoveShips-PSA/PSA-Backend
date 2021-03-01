package com.example.PSABackend.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Date;
import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.*;

//consider modifying sendEmail to take in input so there can be a customised message and customised recipient

@RestController
public class EmailerController {
    @RequestMapping(value = "/sendemail")
    public String sendEmail() {
        return "Email sent successfully";
    }

    private void sendmail() throws AddressException, MessagingException, IOException {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("cs102psa@gmail.com", "cs102javapsadabdab");
            }
        });
        Message msg = new MimeMessage(session);
        msg.setFrom(new InternetAddress("cs102psa@gmail.com", false));

        msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse("shaunts5111@gmail.com"));
        msg.setSubject("Aye yo wat is uppppp");
        msg.setContent("Good pm sir/mdm,", "text/html");
        msg.setSentDate(new Date());

        MimeBodyPart messageBodyPart = new MimeBodyPart();
        messageBodyPart.setContent("CS102 Java PSA Dab Dab", "text/html");

        Multipart multipart = new MimeMultipart();
        multipart.addBodyPart(messageBodyPart);
        MimeBodyPart attachPart = new MimeBodyPart();

        Transport.send(msg);
    }
}
