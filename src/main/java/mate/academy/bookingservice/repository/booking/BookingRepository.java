package mate.academy.bookingservice.repository.booking;

import java.util.List;
import mate.academy.bookingservice.model.Booking;
import mate.academy.bookingservice.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> getBookingsByUserAndStatus(User user, Booking.Status status);

    List<Booking> getBookingsByStatus(Booking.Status status);

    List<Booking> getBookingsByUser(User user);

    List<Booking> findBookingsByAccommodationIdAndStatus(Long accommodationId,
                                                         Booking.Status status);
}
