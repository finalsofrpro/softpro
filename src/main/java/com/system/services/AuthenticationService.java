package com.system.services;

import com.system.models.Role;
import com.system.observers.NotificationObserver;
import java.io.*;
import java.util.*;

/**
 * Service class responsible for user authentication and account management.
 * This class acts as the 'Subject' in the Observer pattern.
 *  @author Raghad  and Farah
 * @version 1.0
 */
public class AuthenticationService {
    private Map<String, String> userAccounts = new HashMap<>(); // يحمل: Username -> Password
    private Map<String, String> userEmails = new HashMap<>();   // يحمل: Username -> Email
    private Map<String, String> adminAccounts = new HashMap<>();

    // قائمة المراقبين (Observer Pattern) - US 3.1
    private List<NotificationObserver> observers = new ArrayList<>();

    private final String USER_FILE = "users.txt";
    private final String ADMIN_FILE = "admins.txt";

    /**
     * Constructor initializes the service, loads data, and registers default observers.
     */
    public AuthenticationService() {
        loadAccounts(USER_FILE, userAccounts);
        loadAccounts(ADMIN_FILE, adminAccounts);

        // إضافة الـ EmailService كمراقب تلقائياً عند تشغيل الخدمة
        addObserver(new EmailService());

        if (adminAccounts.isEmpty()) {
            adminAccounts.put("admin", "admin123");
            saveAccount(ADMIN_FILE, "admin", "admin123");
        }
    }

    /**
     * Adds a new observer to the notification list.
     * @param observer The observer to be added.
     */
    public void addObserver(NotificationObserver observer) {
        if (observer != null) {
            observers.add(observer);
        }
    }

    /**
     * Notifies all registered observers.
     * @param email The recipient email.
     * @param message The message content.
     */
    private void notifyObservers(String email, String message) {
        for (NotificationObserver observer : observers) {
            observer.update(email, message);
        }
    }

    // فحص صيغة اليوزرنيم
    private boolean isValidUsername(String username) {
        return username != null && username.matches("^[a-zA-Z0-9._]+$");
    }

    /**
     * Registers a new user.
     * @param username The unique username.
     * @param password The account password.
     * @param email The user email.
     * @return true if registration is successful.
     */
    public boolean registerNewUser(String username, String password, String email) {
        // 1. فحص الصيغة
        if (!isValidUsername(username)) return false;

        // 2. فحص التكرار (Ignore Case)
        for (String existingUser : userAccounts.keySet()) {
            if (existingUser.equalsIgnoreCase(username)) return false;
        }

        // 3. تخزين البيانات في الـ Maps
        userAccounts.put(username, password);
        userEmails.put(username, email);

        // 4. حفظ في الملف
        saveAccount(USER_FILE, username, password + "," + email);

        // 5. إرسال إشعار عبر الـ Observers في Thread منفصل
        String welcomeMessage = "Hello " + username + ",\n\nYour account has been created successfully!";
        new Thread(() -> notifyObservers(email, welcomeMessage)).start();

        return true;
    }

    /**
     * Authenticates a user or admin.
     * @param username Username input.
     * @param password Password input.
     * @return The user Role.
     */
    public Role login(String username, String password) {
        // فحص الأدمن
        for (Map.Entry<String, String> entry : adminAccounts.entrySet()) {
            if (entry.getKey().equalsIgnoreCase(username) && entry.getValue().equals(password)) {
                return Role.ADMIN;
            }
        }

        // فحص اليوزر
        for (Map.Entry<String, String> entry : userAccounts.entrySet()) {
            if (entry.getKey().equalsIgnoreCase(username) && entry.getValue().equals(password)) {
                return Role.USER;
            }
        }
        return Role.NONE;
    }

    private void saveAccount(String filePath, String user, String data) {
        try (PrintWriter out = new PrintWriter(new FileWriter(filePath, true))) {
            out.println(user + "," + data);
        } catch (IOException e) {
            System.err.println("Error saving: " + e.getMessage());
        }
    }

    private void loadAccounts(String filePath, Map<String, String> map) {
        File file = new File(filePath);
        if (!file.exists()) return;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 2) {
                    map.put(parts[0], parts[1]);
                    if (filePath.equals(USER_FILE) && parts.length == 3) {
                        userEmails.put(parts[0], parts[2]);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading: " + filePath);
        }
    }

    public void logout() {
        System.out.println("[System]: Logged out.");
    }
}