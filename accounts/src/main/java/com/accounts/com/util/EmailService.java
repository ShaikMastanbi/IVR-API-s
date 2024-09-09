package com.accounts.com.util;

import org.springframework.stereotype.Component;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;
@Component
public class EmailService {
    public static void sendMail(String from, String to, String subject, String message) throws MessagingException {

        String host = "smtp.googlemail.com";
        // Get system properties
        Properties properties = System.getProperties();
        System.out.println("PROPERTIES = " + properties);

        // Setting important information to properties object

        // host set
        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.port", "465");
        properties.put("mail.smtp.ssl.enable", "true");
        properties.put("mail.smtp.auth", "true");

        // Session
        //Session session = Session.getDefaultInstance(properties, new Authenticator() {
        Session session = Session.getInstance(properties, new Authenticator() {

            @Override
            protected PasswordAuthentication getPasswordAuthentication() {

                return new PasswordAuthentication("ivrapis43@gmail.com", "wfuxfbimgzlnyrhv");
            }

        });

        session.setDebug(true);

        MimeMessage m = new MimeMessage(session);
        m.setFrom(from);
        m.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
        m.setSubject(subject);
        m.setText(message);

        Transport.send(m);
        System.out.println("Mail sent Successfully...");

    }
}
