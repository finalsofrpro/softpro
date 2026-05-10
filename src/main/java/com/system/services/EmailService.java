package com.system.services;

import com.system.observers.NotificationObserver;
import javax.mail.*;
import javax.mail.internet.*;
import java.util.Properties;

public class EmailService implements NotificationObserver {

    protected String getSenderEmail() {
        return System.getenv("APP_EMAIL");
    }

    protected String getAppPassword() {
        return System.getenv("APP_PASSWORD");
    }

    @Override
    public void update(String recipient, String message) {
        new Thread(() -> sendEmail(recipient, message)).start();
    }

    public void sendEmail(String recipientEmail, String content) {
        String password = getAppPassword();

        if (password == null || password.isEmpty()) {
            throw new IllegalStateException("APP_PASSWORD environment variable is not set!");
        }

        Session session = createSession(password);

        try {
            Message msg = createMessage(session, recipientEmail, content);
            Transport.send(msg);
            System.out.println("✅ Email sent to: " + recipientEmail);
        } catch (MessagingException e) {
            handleException(recipientEmail, e);
        }
    }

    private Session createSession(String password) {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.ssl.protocols", "TLSv1.2");
        props.put("mail.smtp.ssl.trust", "smtp.gmail.com");

        return Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(getSenderEmail(), password);
            }
        });
    }

    private Message createMessage(Session session, String recipientEmail, String content) throws MessagingException {
        Message msg = new MimeMessage(session);
        msg.setFrom(new InternetAddress(getSenderEmail()));
        msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
        msg.setSubject("System Notification - Appointment Update");
        msg.setText(content);
        return msg;
    }


    private void handleException(String recipientEmail, MessagingException e) {
        System.err.println("❌ Failed to send email to " + recipientEmail);
    }
}