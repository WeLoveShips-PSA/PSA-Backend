package com.example.PSABackend.service;

import java.io.IOException;
import java.util.Date;
import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.*;

public class EmailService {

    public static String sendEmail(String email, String body, String head,String userName) throws AddressException, MessagingException, IOException {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        //input username and password
        Session session = Session.getInstance(props, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("cs102psa@gmail.com", "cs102javapsadabdab");
            }
        });

        Message msg = new MimeMessage(session);
        //sender email
        msg.setFrom(new InternetAddress("cs102psa@gmail.com", false));

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
        //success message
        return "Email sent successfully!";
    }


//    public static String changeInPredicted_BTR_sendEmail(String recipient) throws AddressException, MessagingException, IOException {
//        Properties props = new Properties();
//        props.put("mail.smtp.auth", "true");
//        props.put("mail.smtp.starttls.enable", "true");
//        props.put("mail.smtp.host", "smtp.gmail.com");
//        props.put("mail.smtp.port", "587");
//
//        //input username and password
//        Session session = Session.getInstance(props, new javax.mail.Authenticator() {
//            protected PasswordAuthentication getPasswordAuthentication() {
//                return new PasswordAuthentication("cs102psa@gmail.com", "cs102javapsadabdab");
//            }
//        });
//
//        Message msg = new MimeMessage(session);
//        //sender email
//        msg.setFrom(new InternetAddress("cs102psa@gmail.com", false));
//
//        //content should be dependent on alert class
//        //to be changed
//        msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipient));
//        msg.setSubject("Changes in predicted berthing time");
//        msg.setContent("Good pm sir/mdm", "text/html");
//        msg.setSentDate(new Date());
//
//        MimeBodyPart messageBodyPart = new MimeBodyPart();
//        messageBodyPart.setContent("CS102 Java PSA Dab Dab", "text/html");
//
////        Multipart multipart = new MimeMultipart();
////        multipart.addBodyPart(messageBodyPart);
////        MimeBodyPart attachPart = new MimeBodyPart();
//
//        Transport.send(msg);
//        //success message
//        return "Email sent successfully!";
//    }
}