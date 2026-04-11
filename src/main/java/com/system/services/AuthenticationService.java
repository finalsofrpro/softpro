package com.system.services;

import com.system.models.Role;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Raghd Mansour & Farah
 * خدمة التحقق من الهوية - النسخة النهائية (تخزين الإيميل + حماية الصيغة + Ignore Case)
 */
public class AuthenticationService {
    private Map<String, String> userAccounts = new HashMap<>(); // يحمل: Username -> Password
    private Map<String, String> userEmails = new HashMap<>();   // يحمل: Username -> Email
    private Map<String, String> adminAccounts = new HashMap<>();

    private final String USER_FILE = "users.txt";
    private final String ADMIN_FILE = "admins.txt";

    public AuthenticationService() {
        loadAccounts(USER_FILE, userAccounts); // يتم تحميل الإيميلات أيضاً داخل هذه الميثود
        loadAccounts(ADMIN_FILE, adminAccounts);

        if (adminAccounts.isEmpty()) {
            adminAccounts.put("admin", "admin123");
            saveAccount(ADMIN_FILE, "admin", "admin123");
        }
    }

    // فحص صيغة اليوزرنيم (أحرف، أرقام، نقطة، شرطة سفلية فقط)
    private boolean isValidUsername(String username) {
        return username != null && username.matches("^[a-zA-Z0-9._]+$");
    }

    /**
     * تسجيل مستخدم جديد مع الإيميل
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

        // 4. حفظ في الملف (Username,Password,Email)
        saveAccount(USER_FILE, username, password + "," + email);

        // 5. إرسال إيميل ترحيبي في Thread منفصل (عشان البرنامج ما يعلق)
        new Thread(() -> EmailService.sendWelcomeEmail(email, username)).start();

        return true;
    }

    /**
     * تسجيل أدمن جديد (الأدمن لا يحتاج إيميل حالياً حسب الطلب)
     */
    public boolean registerNewAdmin(String username, String password) {
        if (!isValidUsername(username)) return false;

        for (String existingAdmin : adminAccounts.keySet()) {
            if (existingAdmin.equalsIgnoreCase(username)) return false;
        }

        adminAccounts.put(username, password);
        saveAccount(ADMIN_FILE, username, password);
        return true;
    }

    /**
     * فحص الدخول
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

    // ميثود مساعدة للحفظ في الملف
    private void saveAccount(String filePath, String user, String data) {
        try (PrintWriter out = new PrintWriter(new FileWriter(filePath, true))) {
            out.println(user + "," + data);
        } catch (IOException e) {
            System.err.println("Error saving: " + e.getMessage());
        }
    }

    // ميثود مساعدة لتحميل البيانات من الملفات
    private void loadAccounts(String filePath, Map<String, String> map) {
        File file = new File(filePath);
        if (!file.exists()) return;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 2) {
                    map.put(parts[0], parts[1]); // parts[0]=User, parts[1]=Pass

                    // إذا كان الملف هو ملف اليوزرز وفيه إيميل (الخانة الثالثة)
                    if (filePath.equals(USER_FILE) && parts.length == 3) {
                        userEmails.put(parts[0], parts[2]); // parts[2]=Email
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