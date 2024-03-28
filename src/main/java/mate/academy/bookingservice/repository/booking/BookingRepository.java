package mate.academy.bookingservice.repository.booking;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import mate.academy.bookingservice.model.Booking;
import mate.academy.bookingservice.model.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> getBookingsByUserAndStatus(User user, Booking.Status status);

    List<Booking> getBookingsByStatus(Booking.Status status);

    List<Booking> getBookingsByUser(User user);

    List<Booking> getBookingsByCheckOutDate(LocalDate checkOutDate);

    List<Booking> findBookingsByAccommodationIdAndStatus(Long accommodationId,
                                                         Booking.Status status);
    @Modifying
    @Transactional
    @Query("UPDATE Booking SET status = :status WHERE id = :bookingId")
    void updateBookingByIdAndStatus(Long bookingId, Booking.Status status);

    @EntityGraph(attributePaths = {"accommodation", "accommodation.address", "user"})
    @Override
    Optional<Booking> findById(Long aLong);
}
