package com.example.PSABackend.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.Date;
import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.*;
import javax.validation.constraints.Email;

@Repository
public class EmailService {
    private static String email;
    private static String password;
    private static String auth;
    private static String starttlsEnable;
    private static String host;
    private static String port;

    @Value("${portnet.email.from}")
    public void setMail(String value) {
        this.email = value;
    }

    @Value("${portnet.mail.password}")
    public void setPassword(String value) {
        this.password = value;
    }

    @Value("${portnet.mail.properties.mail.smtp.auth}")
    public void setAuth(String value) {
        this.auth = value;
    }
    @Value("${portnet.mail.properties.mail.smtp.starttls.enable}")
    public void setStarttlsEnable(String value) {
        this.starttlsEnable = value;
    }

    @Value("${portnet.mail.properties.mail.smtp.host}")
    public void setHost(String value) {
        this.host = value;
    }

    @Value("${portnet.mail.properties.mail.smtp.port}")
    public void setdPort(String value) {
        this.port = value;
    }

    public static String sendEmail(String email, String body, String head,String userName) throws AddressException, MessagingException, IOException {
        Properties props = new Properties();
        props.put("mail.smtp.auth", EmailService.auth);
        props.put("mail.smtp.starttls.enable", EmailService.starttlsEnable);
        props.put("mail.smtp.host", EmailService.host);
        props.put("mail.smtp.port", EmailService.port);

        //input username and password
        Session session = Session.getInstance(props, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(EmailService.email, EmailService.password);
            }
        });

        Message msg = new MimeMessage(session);
        //sender email
        msg.setFrom(new InternetAddress(EmailService.email, false));

        //content should be dependent on alert class
        //to be changed
        msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email));
        msg.setSubject(head);
        msg.setContent(body, "text/html");
        msg.setSentDate(new Date());
//
//        MimeBodyPart messageBodyPart = new MimeBodyPart();
//        messageBodyPart.setContent(body, "text/html");

//        Multipart multipart = new MimeMultipart();
//        multipart.addBodyPart(messageBodyPart);
//        MimeBodyPart attachPart = new MimeBodyPart();

        Transport.send(msg);
        return "Email sent successfully!";
    }
}