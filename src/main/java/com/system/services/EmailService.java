package com.system.services;

import com.system.observers.NotificationObserver;
import javax.mail.*;
import javax.mail.internet.*;
import java.util.Properties;

public class EmailService implements NotificationObserver {

    private static final String MY_EMAIL = "raghdmansour91@gmail.com";

    // ❗ بدل static final → method عشان التست يقدر يتحكم
    protected String getAppPassword() {
        return System.getenv("APP_PASSWORD");
    }

    @Override
    public void update(String recipient, String message) {
        new Thread(() -> sendEmail(recipient, message)).start();
    }

    public void sendEmail(String recipientEmail, String content) {

        String password = getAppPassword();

        // 🔒 security check (ما تغير)
        if (password == null || password.isEmpty()) {
            throw new IllegalStateException("APP_PASSWORD environment variable is not set!");
        }

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
                return new PasswordAuthentication(MY_EMAIL, password);
            }
        });

        try {
            Message msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(MY_EMAIL));
            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
            msg.setSubject("System Notification - Appointment Update");
            msg.setText(content);

            Transport.send(msg);

            System.out.println("✅ Email sent to: " + recipientEmail);

        } catch (MessagingException e) {
            System.err.println("❌ Failed to send email to " + recipientEmail);
            e.printStackTrace();
        }
    }
}