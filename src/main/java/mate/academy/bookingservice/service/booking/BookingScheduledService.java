package mate.academy.bookingservice.service.booking;

import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import mate.academy.bookingservice.model.Booking;
import mate.academy.bookingservice.repository.booking.BookingRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BookingScheduledService {
    private final BookingService bookingService;
    private final BookingRepository bookingRepository;

    @Scheduled(cron = "0 0 10 * * ?") // doing every day at 10:00
    public void filterAllExpiredBookings() {
        bookingService.getBookingsByCheckOutDate(LocalDate.now()).stream()
                .map(b -> b.setStatus(Booking.Status.EXPIRED))
                .forEach(bookingRepository::save);
        // todo implement telegram message for each EXPIRED booking or else "No expired bookings today!"
    }
}
