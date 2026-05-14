package com.system.services;

import com.system.observers.NotificationObserver;
import javax.mail.*;
import javax.mail.internet.*;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Final Email Service Implementation for Phase 2.
 * Handles real-time notifications for bookings and cancellations.
 * @author Raghad and Farah
 */
public class EmailService implements NotificationObserver {
    private static final Logger LOGGER = Logger.getLogger(EmailService.class.getName());

    // ✅ ضعي بياناتك هنا لمرة واحدة وأخيرة
    private static final String SENDER_EMAIL = "raghdmansour91@gmail.com";
    private static final String APP_PASSWORD = "bxnvcwuxyziukyjn"; // الكود المكون من 16 حرف بدون مسافات

    @Override
    public void update(String recipient, String message) {
        // الإرسال المباشر لضمان وصول الإيميل قبل إغلاق البرنامج أو الشاشة
        sendEmail(recipient, message);
    }

    public void sendEmail(String recipientEmail, String content) {
        // طباعة في الكونسول للتأكد من أن الميثود بدأت العمل
        System.out.println("DEBUG: Sending email to " + recipientEmail + "...");

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.ssl.trust", "smtp.gmail.com");

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(SENDER_EMAIL, APP_PASSWORD);
            }
        });

        try {
            Message msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(SENDER_EMAIL));
            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
            msg.setSubject("Appointment System Update");
            msg.setText(content);

            Transport.send(msg);

            // نجاح الإرسال
            System.out.println("✅✅ SUCCESS: Email actually delivered to " + recipientEmail);
            LOGGER.log(Level.INFO, "Email sent successfully to {0}", recipientEmail);

        } catch (MessagingException e) {
            // في حال حدوث خطأ، سيطبع السبب بالتفصيل في الكونسول
            System.err.println("❌❌ MAIL ERROR: " + e.getMessage());
            LOGGER.log(Level.SEVERE, "Failed to send email", e);
        }
    }
}