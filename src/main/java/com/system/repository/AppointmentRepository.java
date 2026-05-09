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

    // 🔥 هذا المتغير هو الحل
    private boolean useFile;

    // ✅ Constructor للتست (بدون file)
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
                throw new IllegalArgumentException(
                        "Time Conflict! This slot overlaps with an existing appointment ("
                                + existingStart.format(formatter) + " for "
                                + existing.getDurationMinutes() + " min)."
                );
            }
        }

        appointments.add(newApp);

        if (useFile) {
            saveToFile();
        }
    }

    public void deleteAppointment(int id) {
        appointments.removeIf(a -> a.getId() == id);

        if (useFile) {
            saveToFile();
        }
    }

    public List<Appointment> getAvailableAppointments() {
        if (useFile) {
            appointments.clear();
            loadFromFile();
        }
        return appointments;
    }

    // ✅ خليهم public عشان يختفي الخطأ القديم
    public void saveToFile() {
        try (PrintWriter out = new PrintWriter(new FileWriter(FILE_NAME))) {
            for (Appointment app : appointments) {
                out.println(app.getId() + "|" +
                        app.getDateTime() + "|" +
                        app.getDurationMinutes() + "|" +
                        app.getStatus() + "|" +
                        app.getMaxParticipants() + "|" +
                        app.getType() + "|" +
                        app.getBookedBy());
            }
        } catch (IOException e) {
            System.err.println("Error saving to file: " + e.getMessage());
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
                    int id = Integer.parseInt(p[0]);
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
            System.err.println("Error loading file: " + e.getMessage());
        }
    }
}