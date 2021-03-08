package com.example.PSABackend.classes;

import java.io.IOException;
import java.util.Date;
import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.*;

public class Emailer {

//    public static void main(String[] args) {
//        try {
//            System.out.println(sendEmail("rxtay.2020@sis.smu.edu.sg")); //recipient email
//        } catch (Exception e){
//            System.out.println("Error 404: Email not found");
//        }
//    }

    //alert_type decides what kind of message is sent to the email
    public static String sendEmail(String recipient) throws AddressException, MessagingException, IOException {
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
        msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipient));
        msg.setSubject("We Love Ships");
        msg.setContent("Good pm sir/mdm", "text/html");
        msg.setSentDate(new Date());

        MimeBodyPart messageBodyPart = new MimeBodyPart();
        messageBodyPart.setContent("CS102 Java PSA Dab Dab", "text/html");

//        Multipart multipart = new MimeMultipart();
//        multipart.addBodyPart(messageBodyPart);
//        MimeBodyPart attachPart = new MimeBodyPart();

        Transport.send(msg);
        //success message
        return "Email sent successfully!";
    }
}