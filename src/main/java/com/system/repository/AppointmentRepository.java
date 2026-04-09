package com.system.repository;

import com.system.models.Appointment;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;


 // مستودع المواعيد - يدعم التخزين في appointments.txt بـ 5 أعمدة


public class AppointmentRepository {
    private List<Appointment> appointments = new ArrayList<>();
    private final String FILE_NAME = "appointments.txt";
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public AppointmentRepository() {
        loadFromFile(); // تحميل المواعيد المخزنة فور استدعاء الكلاس
    }

    public void addAppointment(Appointment newApp) {
        LocalDateTime newStart = newApp.getDateTime();
        LocalDateTime newEnd = newStart.plusMinutes(newApp.getDurationMinutes());

        for (Appointment existing : appointments) {
            // فحص الـ ID المكرر
            if (existing.getId() == newApp.getId()) {
                throw new IllegalArgumentException("Appointment ID already exists!");
            }

            // فحص تضارب الوقت (Overlapping)
            LocalDateTime existingStart = existing.getDateTime();
            LocalDateTime existingEnd = existingStart.plusMinutes(existing.getDurationMinutes());

            if (newStart.isBefore(existingEnd) && newEnd.isAfter(existingStart)) {
                throw new IllegalArgumentException("Time Conflict! This slot overlaps with an existing appointment ("
                        + existingStart.format(formatter) + " for " + existing.getDurationMinutes() + " min).");
            }
        }

        appointments.add(newApp);
        saveToFile();
    }

    public void deleteAppointment(int id) {
        appointments.removeIf(a -> a.getId() == id);
        saveToFile();
    }

    public List<Appointment> getAvailableAppointments() {
        // نحدث القائمة من الملف قبل الإرجاع للتأكد من مزامنة البيانات
        appointments.clear();
        loadFromFile();
        return appointments;
    }



    public void saveToFile() {
        // نستخدم PrintWriter بدون true لمسح الملف وكتابة القائمة المحدثة (Overwriting)
        // هذا يضمن عدم تكرار المواعيد عند كل عملية حفظ
        try (PrintWriter out = new PrintWriter(new FileWriter(FILE_NAME))) {
            for (Appointment app : appointments) {
                // التأكد من وجود قيمة للـ BookedBy لتجنب الـ Null
                String bookedBy = (app.getBookedBy() == null || app.getBookedBy().isEmpty()) ? "NONE" : app.getBookedBy();

                out.println(app.getId() + "," +
                        app.getDateTime().format(formatter) + "," +
                        app.getDurationMinutes() + "," +
                        app.getStatus() + "," +
                        bookedBy);
            }
        } catch (IOException e) {
            System.err.println("Error saving appointments: " + e.getMessage());
        }
    }

    private void loadFromFile() {
        File file = new File(FILE_NAME);
        if (!file.exists()) return;

        // تنظيف القائمة قبل التحميل لمنع التكرار في الذاكرة
        appointments.clear();

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 5) {
                    Appointment app = new Appointment(
                            Integer.parseInt(parts[0]),
                            LocalDateTime.parse(parts[1], formatter),
                            Integer.parseInt(parts[2])
                    );
                    app.setStatus(parts[3]);
                    app.setBookedBy(parts[4].equals("NONE") ? "" : parts[4]);
                    appointments.add(app);
                }
            }
        } catch (IOException | RuntimeException e) {
            System.err.println("Error loading appointments: " + e.getMessage());
        }
    }
}