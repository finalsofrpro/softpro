package com.system.services;

import com.system.models.Role;
import java.util.HashMap;
import java.util.Map;

public class AuthenticationService {
    // مخزن مؤقت لليوزرز (Username -> Password)
    private Map<String, String> users = new HashMap<>();

    public AuthenticationService() {
        // حساب الأدمن ثابت للنظام
        users.put("admin", "admin123");
        // حساب تجريبي لليوزر
        users.put("user", "user123");
    }

    // ميزة إنشاء حساب جديد (US الجديد)
    public void register(String username, String password) {
        users.put(username, password);
        System.out.println("[Success]: تم إنشاء الحساب بنجاح! يمكنك الآن تسجيل الدخول.");
    }

    public Role login(String username, String password) {
        if (users.containsKey(username) && users.get(username).equals(password)) {
            return username.equals("admin") ? Role.ADMIN : Role.USER;
        }
        return Role.NONE;
    }

    public void logout() {
        System.out.println("[System]: تم تسجيل الخروج.");
    }
}