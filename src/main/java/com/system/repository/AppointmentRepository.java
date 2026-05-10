package com.system.repository;

import com.system.models.Appointment;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class AppointmentRepository {
    private List<Appointment> appointments = new ArrayList<>();
    private final String FILE_NAME = "appointments.txt";
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private boolean useFile;

    // ✅ Constructor للتست (بدون ملف)
    public AppointmentRepository() {
        this.useFile = false;
    }

    // ✅ Constructor للتشغيل الحقيقي
    public AppointmentRepository(boolean useFile) {
        this.useFile = useFile;
        if (useFile) {
            loadFromFile();
        }
    }

    public void addAppointment(Appointment newApp) {
        LocalDateTime newStart = newApp.getDateTime();
        LocalDateTime newEnd = newStart.plusMinutes(newApp.getDurationMinutes());

        for (Appointment existing : appointments) {
            if (existing.getId() == newApp.getId()) {
                throw new IllegalArgumentException("Appointment ID already exists!");
            }
            LocalDateTime existingStart = existing.getDateTime();
            LocalDateTime existingEnd = existingStart.plusMinutes(existing.getDurationMinutes());

            if (newStart.isBefore(existingEnd) && newEnd.isAfter(existingStart)) {
                throw new IllegalArgumentException("Time Conflict! Overlaps with: " + existingStart.format(formatter));
            }
        }
        appointments.add(newApp);
        if (useFile) saveToFile();
    }

    public void deleteAppointment(int id) {
        appointments.removeIf(a -> a.getId() == id);
        if (useFile) saveToFile();
    }

    public List<Appointment> getAvailableAppointments() {
        if (useFile) {
            loadFromFile();
        }
        return appointments;
    }

    public void saveToFile() {
        try (PrintWriter out = new PrintWriter(new FileWriter(FILE_NAME))) {
            for (Appointment app : appointments) {
                out.println(app.getId() + "|" + app.getDateTime() + "|" +
                        app.getDurationMinutes() + "|" + app.getStatus() + "|" +
                        app.getMaxParticipants() + "|" + app.getType() + "|" + app.getBookedBy());
            }
        } catch (IOException e) {
            System.err.println("Error saving: " + e.getMessage());
        }
    }

    public void loadFromFile() {
        appointments.clear();
        File file = new File(FILE_NAME);
        if (!file.exists()) return;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] p = line.split("\\|");
                if (p.length >= 6) {
                    Appointment app = new Appointment(
                            Integer.parseInt(p[0]),
                            LocalDateTime.parse(p[1]),
                            Integer.parseInt(p[2]),
                            Integer.parseInt(p[4]),
                            p[5]
                    );
                    app.setStatus(p[3]);
                    app.setBookedBy((p.length > 6) ? p[6] : "");
                    appointments.add(app);
                }
            }
        } catch (Exception e) {
            System.err.println("Error loading: " + e.getMessage());
        }
    }
}