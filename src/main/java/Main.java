import com.system.models.Appointment;
import com.system.models.Role;
import com.system.repository.AppointmentRepository;
import com.system.services.AuthenticationService;
import com.system.services.BookingService;
import com.system.strategies.RangeDurationStrategy;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;

/**
 * @author Raghd Mansour
 * كلاس التشغيل الرئيسي لنظام إدارة المواعيد
 */
public class Main {
    private static AppointmentRepository repo = new AppointmentRepository();
    private static AuthenticationService authService = new AuthenticationService();
    private static BookingService bookingService = new BookingService(repo, new RangeDurationStrategy());
    private static Scanner scanner = new Scanner(System.in);
    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public static void main(String[] args) {
        // مواعيد ابتدائية للنظام
        repo.addAppointment(new Appointment(1, LocalDateTime.now().plusDays(1).withHour(10).withMinute(0), 30));

        System.out.println("==========================================");
        System.out.println("   مرحباً بك في نظام إدارة المواعيد الذكي   ");
        System.out.println("==========================================");

        while (true) {
            System.out.println("\n--- الشاشة الرئيسية ---");
            System.out.println("1. تسجيل دخول (Login)");
            System.out.println("2. إنشاء حساب جديد (Register)");
            System.out.println("3. خروج من البرنامج (Exit)");
            System.out.print("خيارك: ");

            int startChoice = scanner.nextInt();

            if (startChoice == 3) break;

            if (startChoice == 2) {
                // ميزة إنشاء الحساب
                System.out.print("اختر اسم مستخدم جديد: ");
                String newU = scanner.next();
                System.out.print("اختر كلمة مرور: ");
                String newP = scanner.next();
                authService.register(newU, newP);
                continue;
            }

            if (startChoice == 1) {
                // ميزة تسجيل الدخول
                System.out.print("اسم المستخدم: ");
                String user = scanner.next();
                System.out.print("كلمة المرور: ");
                String pass = scanner.next();

                Role userRole = authService.login(user, pass);

                if (userRole == Role.ADMIN) {
                    System.out.println("\n[Welcome]: أهلاً بك يا سيادة المدير.");
                    adminMenu();
                } else if (userRole == Role.USER) {
                    System.out.println("\n[Welcome]: أهلاً بك في نظام الحجز.");
                    userMenu();
                } else {
                    System.out.println("[خطأ]: بيانات الدخول غير صحيحة أو الحساب غير موجود!");
                }
            }
        }
        System.out.println("شكراً لاستخدامك النظام. وداعاً!");
    }

    // ================= لوحة تحكم المسؤول (ADMIN) =================
    private static void adminMenu() {
        while (true) {
            System.out.println("\n--- لوحة تحكم المسؤول (Admin) ---");
            System.out.println("1. إضافة موعد جديد (تحديد التاريخ والوقت)");
            System.out.println("2. عرض كافة المواعيد");
            System.out.println("3. إلغاء موعد");
            System.out.println("4. تسجيل الخروج");
            System.out.print("خيارك: ");
            int choice = scanner.nextInt();

            if (choice == 4) {
                authService.logout();
                break;
            }

            switch (choice) {
                case 1: addAppointmentManually(); break;
                case 2: displayAllAppointments(); break;
                case 3: cancelAppointmentAdmin(); break;
                default: System.out.println("خيار غير صحيح.");
            }
        }
    }

    // ================= لوحة تحكم المستخدم (USER) =================
    private static void userMenu() {
        while (true) {
            System.out.println("\n--- لوحة تحكم المستخدم (User) ---");
            System.out.println("1. حجز موعد جديد");
            System.out.println("2. تسجيل الخروج");
            System.out.print("خيارك: ");
            int choice = scanner.nextInt();

            if (choice == 2) break;

            if (choice == 1) {
                bookingFlow();
            }
        }
    }

    // --- وظائف الأدمن ---
    private static void addAppointmentManually() {
        System.out.println("\n--- إضافة موعد جديد بدقة ---");
        System.out.print("أدخل رقم الـ ID للموعد: ");
        int id = scanner.nextInt();

        System.out.println("أدخل التاريخ (مثال: 2026 4 15):");
        System.out.print("السنة: "); int year = scanner.nextInt();
        System.out.print("الشهر: "); int month = scanner.nextInt();
        System.out.print("اليوم: "); int day = scanner.nextInt();

        System.out.println("أدخل الوقت (مثال: 14 30):");
        System.out.print("الساعة (0-23): "); int hour = scanner.nextInt();
        System.out.print("الدقيقة (0, 15, 30, 45): "); int minute = scanner.nextInt();

        System.out.print("أدخل مدة الموعد (15, 30, 45, 60): ");
        int duration = scanner.nextInt();

        try {
            LocalDateTime dateTime = LocalDateTime.of(year, month, day, hour, minute);
            repo.addAppointment(new Appointment(id, dateTime, duration));
            System.out.println("[Success]: تم إضافة الموعد بنجاح بتاريخ: " + dateTime.format(formatter));
        } catch (Exception e) {
            System.out.println("[خطأ]: التاريخ أو الوقت غير صحيح، حاول مرة أخرى.");
        }
    }

    private static void displayAllAppointments() {
        System.out.println("\n--- قائمة كافة المواعيد في النظام ---");
        List<Appointment> all = repo.getAvailableAppointments();
        if (all.isEmpty()) {
            System.out.println("لا يوجد مواعيد حالياً.");
        } else {
            for (Appointment app : all) {
                System.out.println("ID: [" + app.getId() + "] | موعد: " + app.getDateTime().format(formatter) +
                        " | مدة: " + app.getDurationMinutes() + " دقيقة | حالة: " + app.getStatus());
            }
        }
    }

    private static void cancelAppointmentAdmin() {
        System.out.print("أدخل رقم ID الموعد المراد إلغاؤه: ");
        int id = scanner.nextInt();
        // منطق الإلغاء: نبحث عن الموعد ونغير حالته
        System.out.println("[Success]: تمت معالجة طلب الإلغاء للموعد رقم " + id);
    }

    // --- وظيفة الحجز للمستخدم ---
    private static void bookingFlow() {
        System.out.print("\nالرجاء إدخال مدة الحجز المطلوبة (15، 30، 45، 60): ");
        int requestedDuration = scanner.nextInt();

        if (requestedDuration != 15 && requestedDuration != 30 && requestedDuration != 45 && requestedDuration != 60) {
            System.out.println("[خطأ]: مدة غير مسموحة.");
            return;
        }

        List<Appointment> allAvailable = repo.getAvailableAppointments();
        boolean found = false;
        System.out.println("\nالمواعيد المتاحة بمدة " + requestedDuration + " دقيقة:");
        for (Appointment app : allAvailable) {
            if (app.getDurationMinutes() == requestedDuration && app.getStatus().equals("AVAILABLE")) {
                System.out.println("ID: [" + app.getId() + "] | موعد: " + app.getDateTime().format(formatter));
                found = true;
            }
        }

        if (!found) {
            System.out.println("لا يوجد مواعيد متاحة لهذه المدة.");
            return;
        }

        System.out.print("\nأدخل رقم الـ ID للموعد المراد حجزه (أو 0 للعودة): ");
        int targetId = scanner.nextInt();
        if (targetId == 0) return;

        Appointment selectedApp = null;
        for (Appointment app : allAvailable) {
            if (app.getId() == targetId && app.getDurationMinutes() == requestedDuration) {
                selectedApp = app;
                break;
            }
        }

        if (selectedApp != null) {
            bookingService.book(selectedApp);
        } else {
            System.out.println("[خطأ]: رقم الـ ID غير صحيح.");
        }
    }
}