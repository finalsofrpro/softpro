package com.system.services;

import com.system.observers.NotificationObserver;
import javax.mail.*;
import javax.mail.internet.*;
import java.util.Properties;

/**
 * Service responsible for sending real-time email notifications to users.
 * This class implements the {@link NotificationObserver} interface as part of the Observer Pattern (US3.1).
 * It ensures the system can send reminders and confirmation updates asynchronously.
 * * @author Raghad and Farah
 * @version 1.0
 */
public class EmailService implements NotificationObserver {

    private static final String MY_EMAIL = "raghdmansour91@gmail.com";
    /** App-specific password for secure SMTP authentication. */
    private static final String APP_PASSWORD = "ebnoqizmsthoewjq";

    /**
     * Receives updates from the subject and triggers the email sending process.
     * To maintain system responsiveness (non-blocking), it executes the email logic in a separate thread.
     * * @param recipient The email address of the user to receive the notification.
     * @param message   The content/body of the notification.
     */
    @Override
    public void update(String recipient, String message) {
        // Run in a separate thread to prevent GUI/Service freezing
        new Thread(() -> sendEmail(recipient, message)).start();
    }

    /**
     * Configures the SMTP server and handles the low-level JavaMail transmission.
     * * @param recipientEmail The target user's email address.
     * @param content        The plain text content of the email.
     */
    public void sendEmail(String recipientEmail, String content) {
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

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(MY_EMAIL));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
            message.setSubject("System Notification - Appointment Update");
            message.setText(content);

            Transport.send(message);
            System.out.println("✅ DONE! Email successfully sent to: " + recipientEmail);
        } catch (MessagingException e) {
            System.err.println("❌ Error: Failed to send email to " + recipientEmail);
            e.printStackTrace();
        }
    }
}