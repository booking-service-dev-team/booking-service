package mate.academy.bookingservice.service.booking;

import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import mate.academy.bookingservice.model.Booking;
import mate.academy.bookingservice.repository.booking.BookingRepository;
import mate.academy.bookingservice.service.notification.NotificationService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BookingScheduledService {
    private final BookingService bookingService;
    private final BookingRepository bookingRepository;
    private final NotificationService notificationService;

    @Scheduled(cron = "0 0 10 * * *") // doing every day at 10:00
    public void filterAllExpiredBookings() {
        List<Booking> bookingsWithCheckOutToday = bookingService
                .getBookingsByCheckOutDate(LocalDate.now());
        if (bookingsWithCheckOutToday.isEmpty()) {
            notificationService.sendMessageToAdmins("No expired bookings today!");
            return;
        }
        bookingsWithCheckOutToday.forEach(booking -> {
            booking.setStatus(Booking.Status.EXPIRED);
            notificationService.sendMessageToAdmins(createMessageOfExpiredBooking(booking));
        });
        bookingRepository.saveAll(bookingsWithCheckOutToday);
    }

    private String createMessageOfExpiredBooking(Booking booking) {
        return "booking with ID: " + booking.getId() + " was EXPIRED";
    }
}
