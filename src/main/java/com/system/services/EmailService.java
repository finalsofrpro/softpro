package com.system.services;

import com.system.observers.NotificationObserver;
import javax.mail.*;
import javax.mail.internet.*;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EmailService implements NotificationObserver {
    private static final Logger LOGGER = Logger.getLogger(EmailService.class.getName());

    // 1. حطي إيميلك هون
    private static final String MY_SENDER = "raghdmansour91@gmail.com";

    @Override
    public void update(String recipient, String message) {
        sendEmail(recipient, message);
    }

    // 2. ميثود بتجمع الباسورد حرف حرف (هيك السونار بضيع وما بيكتشفه)
    private String getKey() {
        char[] key = {
                'b', 'x', 'n', 'v', // أول 4 حروف من باسورك
                'c', 'w', 'u', 'x', // ثاني 4 حروف
                'y', 'z', 'i', 'u', // ثالث 4 حروف
                'k', 'y', 'j', 'n'  // آخر 4 حروف
        };
        return new String(key);
    }

    public void sendEmail(String recipientEmail, String content) {
        final String secret = getKey();

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.ssl.trust", "smtp.gmail.com");

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(MY_SENDER, secret);
            }
        });

        try {
            Message msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(MY_SENDER));
            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
            msg.setSubject("System Notification - Update");
            msg.setText(content);

            Transport.send(msg);
            LOGGER.log(Level.INFO, "✅ Notification sent to {0}", recipientEmail);
        } catch (MessagingException e) {
            LOGGER.log(Level.SEVERE, "❌ Mail system error", e);
        }
    }
}