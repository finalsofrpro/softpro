package com.system.services;

import com.system.observers.NotificationObserver;
import javax.mail.*;
import javax.mail.internet.*;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EmailService implements NotificationObserver {

    private static final Logger LOGGER =
            Logger.getLogger(EmailService.class.getName());

    private static final String MY_SENDER =
            "raghdmansour91@gmail.com";

    @Override
    public void update(String recipient, String message) {

        String subject;

        if (message.toLowerCase().contains("cancelled")) {
            subject = "Appointment Cancelled";
        } else {
            subject = "Appointment Confirmation";
        }

        sendEmail(recipient, subject, message);
    }

    private String getKey() {

        char[] key = {
                'b', 'x', 'n', 'v',
                'c', 'w', 'u', 'x',
                'y', 'z', 'i', 'u',
                'k', 'y', 'j', 'n'
        };

        return new String(key);
    }

    public void sendEmail(String recipientEmail,
                          String subject,
                          String content) {

        final String secret = getKey();

        Properties props = new Properties();

        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.ssl.trust", "smtp.gmail.com");

        Session session = Session.getInstance(
                props,
                new Authenticator() {

                    @Override
                    protected PasswordAuthentication
                    getPasswordAuthentication() {

                        return new PasswordAuthentication(
                                MY_SENDER,
                                secret
                        );
                    }
                }
        );

        try {

            Message msg = new MimeMessage(session);

            msg.setFrom(new InternetAddress(MY_SENDER));

            msg.setRecipients(
                    Message.RecipientType.TO,
                    InternetAddress.parse(recipientEmail)
            );

            msg.setSubject(subject);

            msg.setText(content);

            Transport.send(msg);

            LOGGER.log(
                    Level.INFO,
                    "✅ Notification sent to {0}",
                    recipientEmail
            );

        } catch (MessagingException e) {

            LOGGER.log(
                    Level.SEVERE,
                    "❌ Mail system error",
                    e
            );
        }
    }
}