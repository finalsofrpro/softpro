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
        try (PrintWriter out = new PrintWriter(new FileWriter("appointments.txt"))) {
            for (Appointment app : appointments) {
                // الترتيب: ID, DateTime, Duration, Status, MaxParticipants, Type, BookedBy
                out.println(app.getId() + "|" +
                        app.getDateTime() + "|" +
                        app.getDurationMinutes() + "|" +
                        app.getStatus() + "|" +
                        app.getMaxParticipants() + "|" +
                        app.getType() + "|" +
                        app.getBookedBy());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadFromFile() {
        appointments.clear();
        File file = new File("appointments.txt");
        if (!file.exists()) return;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;

                // بنقسم السطر باستخدام الـ Pipe |
                String[] p = line.split("\\|");

                if (p.length >= 6) {
                    int id = Integer.parseInt(p[0]);
                    // LocalDateTime.parse بدون فورمارتر بتعرف تقرأ التاريخ اللي فيه حرف T تلقائياً
                    LocalDateTime dt = LocalDateTime.parse(p[1]);
                    int dur = Integer.parseInt(p[2]);
                    String status = p[3];
                    int max = Integer.parseInt(p[4]);
                    String type = p[5];
                    String bookedBy = (p.length > 6) ? p[6] : "";

                    Appointment app = new Appointment(id, dt, dur, max, type);
                    app.setStatus(status);
                    app.setBookedBy(bookedBy);
                    appointments.add(app);
                }
            }
        } catch (Exception e) {
            System.out.println("Error loading: " + e.getMessage());
        }
    }
}