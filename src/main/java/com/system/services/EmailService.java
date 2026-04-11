package com.system.services;

import com.system.observers.NotificationObserver;
import javax.mail.*;
import javax.mail.internet.*;
import java.util.Properties;

/**
 * Service responsible for sending email notifications.
 * Implements NotificationObserver for pattern compliance.
 * @author Raghd and Farah
 */
public class EmailService implements NotificationObserver {

    private static final String MY_EMAIL = "raghdmansour91@gmail.com";
    private static final String APP_PASSWORD = "ebnoqizmsthoewjq";

    /**
     * Implementation of the Observer update method.
     * @param recipient The recipient's email address.
     * @param message The content to send.
     */
    @Override
    public void update(String recipient, String message) {
        sendWelcomeEmail(recipient, message);
    }

    public void sendWelcomeEmail(String recipientEmail, String content) {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.ssl.protocols", "TLSv1.2");
        props.put("mail.smtp.ssl.trust", "smtp.gmail.com");

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(MY_EMAIL, APP_PASSWORD);
            }
        });

        session.setDebug(true);

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(MY_EMAIL));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
            message.setSubject("System Notification");
            message.setText(content);

            Transport.send(message);
            System.out.println("✅ DONE! Email sent to: " + recipientEmail);
        } catch (MessagingException e) {
            System.err.println("❌ Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}