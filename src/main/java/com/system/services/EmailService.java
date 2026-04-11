package com.system.services;

import com.system.observers.NotificationObserver;
import javax.mail.*;
import javax.mail.internet.*;
import java.util.Properties;

/**
 * Service responsible for sending email notifications.
 * Implements NotificationObserver for pattern compliance.
 */
public class EmailService implements NotificationObserver {

    private static final String MY_EMAIL = "raghdmansour91@gmail.com";
    // ملاحظة: تأكدي أن هذه هي كلمة مرور التطبيق (App Password) وليس كلمة مرور الإيميل العادية
    private static final String APP_PASSWORD = "ebnoqizmsthoewjq";

    /**
     * Implementation of the Observer update method.
     * Uses a Thread to ensure the application remains responsive during email sending.
     */
    @Override
    public void update(String recipient, String message) {
        // تشغيل عملية الإرسال في Thread منفصل لعدم تجميد واجهة المستخدم (GUI)
        new Thread(() -> sendEmail(recipient, message)).start();
    }

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