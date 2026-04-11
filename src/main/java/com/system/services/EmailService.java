package com.system.services;

import javax.mail.*;
import javax.mail.internet.*;
import java.util.Properties;

public class EmailService {
    // تأكدي إن الإيميل صحيح 100% وبدون مسافات قبله أو بعده
    private static final String MY_EMAIL = "raghdmansour91@gmail.com";

    // انسخي الكود الـ 16 حرف بدون مسافات نهائياً
    private static final String APP_PASSWORD = "ebnoqizmsthoewjq";

    public static void sendWelcomeEmail(String recipientEmail, String username) {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true"); // تفعيل التشفير
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        // إعدادات إضافية لحل مشاكل الاتصال في الإصدارات الحديثة
        props.put("mail.smtp.ssl.protocols", "TLSv1.2");
        props.put("mail.smtp.ssl.trust", "smtp.gmail.com");

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(MY_EMAIL, APP_PASSWORD);
            }
        });

        // تفعيل الـ Debug عشان تشوفي شو بصير بين الكود وجوجل بالظبط في الكونسول
        session.setDebug(true);

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(MY_EMAIL));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
            message.setSubject("Welcome to Our System! 🎉");
            message.setText("Hello " + username + ",\n\nYour account has been created successfully!");

            Transport.send(message);
            System.out.println("✅ DONE! Email sent to: " + recipientEmail);
        } catch (MessagingException e) {
            System.err.println("❌ Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}