package com.system.services;

import com.system.models.Appointment;
import com.system.repository.AppointmentRepository;
import com.system.strategies.BookingStrategy;

public class BookingService {
    private AppointmentRepository repository;
    private BookingStrategy strategy;

    // بنعطي الخدمة المخزن والقانون اللي بدنا نمشي عليه
    public BookingService(AppointmentRepository repository, BookingStrategy strategy) {
        this.repository = repository;
        this.strategy = strategy;
    }

    public boolean book(Appointment appointment) {
        // 1. نتأكد إذا الموعد متاح أصلاً
        if (!"AVAILABLE".equals(appointment.getStatus())) {
            System.out.println("[Error]: الموعد محجوز مسبقاً!");
            return false;
        }

        // 2. نطبق القانون (الـ Strategy)
        if (strategy.isValid(appointment)) {
            appointment.setStatus("BOOKED");
            System.out.println("[Success]: تم حجز الموعد رقم " + appointment.getId() + " بنجاح.");
            return true;
        } else {
            System.out.println("[Failed]: الموعد مرفوض! (تجاوز المدة المسموحة).");
            return false;
        }
    }
}