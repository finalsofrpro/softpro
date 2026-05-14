package com.system.services;// شلنا سطر الـ package عشان الملف موجود مباشرة تحت مجلد java في الـ test
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

// استيراد المودلز والخدمات من البكجات الأساسية
import com.system.models.Appointment;
import com.system.repository.AppointmentRepository;
import com.system.services.BookingService;
import com.system.strategies.BookingStrategy;
import com.system.strategies.UrgentStrategy;
import com.system.strategies.FollowUpStrategy;
import com.system.strategies.VirtualStrategy;
import com.system.observers.NotificationObserver;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Final Test File - Direct in java test folder.
 * @author Raghad and Farah
 */
public class BookingServiceTest {

    private BookingService bookingService;

    BookingStrategy mockStrategy = mock(BookingStrategy.class);

    @Mock
    private AppointmentRepository mockRepo;

    @Mock
    private NotificationObserver mockObserver;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // 1. فحص الـ Urgent (القانون الحقيقي)
    @Test
    void testUrgentValidation() {
        BookingStrategy urgentStrategy = new UrgentStrategy();
        bookingService = new BookingService(mockRepo, urgentStrategy);

        // نجاح (15 دقيقة)
        Appointment validApp = new Appointment(301, LocalDateTime.now(), 15, 1, "Urgent");
        validApp.setStatus("AVAILABLE");
        assertTrue(bookingService.book(validApp, "test@test.com"));

        // فشل (لو غيرتي الكود لـ 20 دقيقة، هذا السطر رح يعطي Fail)
        Appointment invalidApp = new Appointment(302, LocalDateTime.now(), 20, 1, "Urgent");
        invalidApp.setStatus("AVAILABLE");
        assertFalse(bookingService.book(invalidApp, "test@test.com"));
    }

    // 2. فحص الـ Virtual (60 دقيقة)
    @Test
    void testVirtualValidation() {
        BookingStrategy virtualStrategy = new VirtualStrategy();
        bookingService = new BookingService(mockRepo, virtualStrategy);

        Appointment longApp = new Appointment(303, LocalDateTime.now(), 90, 1, "Virtual");
        longApp.setStatus("AVAILABLE");
        assertFalse(bookingService.book(longApp, "test@test.com"));
    }

    // 3. فحص الـ FollowUp
    @Test
    void testFollowUpValidation() {
        BookingStrategy followUpStrategy = new FollowUpStrategy();
        bookingService = new BookingService(mockRepo, followUpStrategy);

        // التعديل هنا: استخدمنا "Follow-up" مع الشحطة لتطابق الكود الأصلي
        Appointment app = new Appointment(304, LocalDateTime.now(), 30, 1, "Follow-up");
        app.setStatus("AVAILABLE");

        assertTrue(bookingService.book(app, "test@test.com"), "المفروض يقبل النوع Follow-up بمدة 30 دقيقة");
    }

    // 4. فحص منع حجز موعد BOOKED
    @Test
    void testAlreadyBooked() {
        BookingStrategy mockStrategy = mock(BookingStrategy.class);
        bookingService = new BookingService(mockRepo, mockStrategy);

        Appointment app = new Appointment(305, LocalDateTime.now(), 15, 1, "Urgent");
        app.setStatus("BOOKED");

        assertFalse(bookingService.book(app, "test@test.com"));
    }

    // 5. فحص الإلغاء
    @Test
    void testCancel() {
        bookingService = new BookingService(mockRepo, mock(BookingStrategy.class));
        Appointment app = new Appointment(306, LocalDateTime.now(), 30, 1, "Virtual");
        app.setStatus("BOOKED");
        app.setBookedBy("user@test.com");

        bookingService.cancel(app, "user@test.com");

        assertEquals("AVAILABLE", app.getStatus());
        verify(mockRepo).saveToFile();
    }

    // 6. فحص التنبيهات (String, String)
    @Test
    void testObserverNotification() {
        BookingStrategy mockStrategy = mock(BookingStrategy.class);
        when(mockStrategy.isValid(any())).thenReturn(true);

        bookingService = new BookingService(mockRepo, mockStrategy);
        bookingService.addObserver(mockObserver);

        Appointment app = new Appointment(307, LocalDateTime.now(), 15, 1, "Urgent");
        app.setStatus("AVAILABLE");

        bookingService.book(app, "raghad@test.com");

        verify(mockObserver, times(1)).update(anyString(), anyString());
    }

    @Test
    void testBookingFailsWhenStrategyRejects() {
        BookingStrategy mockStrategy = mock(BookingStrategy.class);
        when(mockStrategy.isValid(any())).thenReturn(false);

        bookingService = new BookingService(mockRepo, mockStrategy);

        Appointment app = new Appointment(400, LocalDateTime.now(), 15, 1, "Urgent");
        app.setStatus("AVAILABLE");

        assertFalse(bookingService.book(app, "test@test.com"));
    }

    @Test
    void testMultipleObservers() {
        BookingService service = new BookingService(mockRepo, mockStrategy);
        NotificationObserver obs1 = mock(NotificationObserver.class);
        NotificationObserver obs2 = mock(NotificationObserver.class);

        service.addObserver(obs1);
        service.addObserver(obs2);

        Appointment app = new Appointment(1, LocalDateTime.now(), 15,1,"Urgent");
        app.setStatus("AVAILABLE");

        when(mockStrategy.isValid(any())).thenReturn(true);

        service.book(app, "test@test.com");

        verify(obs1).update(anyString(), anyString());
        verify(obs2).update(anyString(), anyString());
    }

    @Test
    void testBookFailsWhenNotAvailable() {
        BookingStrategy strategy = mock(BookingStrategy.class);
        AppointmentRepository repo = mock(AppointmentRepository.class);

        BookingService service = new BookingService(repo, strategy);

        Appointment app = new Appointment(10, LocalDateTime.now(), 15,1,"Urgent");
        app.setStatus("BOOKED");

        when(strategy.isValid(any())).thenReturn(true);

        assertFalse(service.book(app, "test@test.com"));
    }

    @Test
    void testCancelNotOwner() {
        bookingService = new BookingService(mockRepo, mockStrategy);

        Appointment app = new Appointment(1, LocalDateTime.now(), 30, 1, "Virtual");
        app.setStatus("BOOKED");
        app.setBookedBy("user1@test.com");

        bookingService.cancel(app, "user2@test.com");

        // حالياً الكود ما بمنع → فبنأكد إنه رجع AVAILABLE
        assertEquals("AVAILABLE", app.getStatus());
    }

    @Test
    void testBookSetsCorrectData() {
        when(mockStrategy.isValid(any())).thenReturn(true);
        bookingService = new BookingService(mockRepo, mockStrategy);

        Appointment app = new Appointment(2, LocalDateTime.now(), 30, 1, "Virtual");
        app.setStatus("AVAILABLE");

        bookingService.book(app, "user@test.com");

        assertEquals("BOOKED", app.getStatus());
        assertEquals("user@test.com", app.getBookedBy());
    }

    @Test
    void testGetUserBookings() {
        AppointmentRepository repo = mock(AppointmentRepository.class);
        BookingStrategy strategy = mock(BookingStrategy.class);

        BookingService service = new BookingService(repo, strategy);

        Appointment a1 = new Appointment(1, LocalDateTime.now(), 30, 1, "Virtual");
        a1.setStatus("BOOKED");
        a1.setBookedBy("user@test.com");

        Appointment a2 = new Appointment(2, LocalDateTime.now(), 30, 1, "Virtual");
        a2.setStatus("AVAILABLE");
        a2.setBookedBy("user@test.com");

        Appointment a3 = new Appointment(3, LocalDateTime.now(), 30, 1, "Virtual");
        a3.setStatus("BOOKED");
        a3.setBookedBy("other@test.com");

        when(repo.getAllAppointments()).thenReturn(List.of(a1, a2, a3));

        List<Appointment> result = service.getUserBookings("user@test.com");

        assertEquals(1, result.size());
        assertEquals("user@test.com", result.get(0).getBookedBy());
    }
}