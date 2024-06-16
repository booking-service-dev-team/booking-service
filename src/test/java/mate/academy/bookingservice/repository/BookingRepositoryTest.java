package mate.academy.bookingservice.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;
import mate.academy.bookingservice.AbstractIntegrationTest;
import mate.academy.bookingservice.model.Booking;
import mate.academy.bookingservice.repository.booking.BookingRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class BookingRepositoryTest extends AbstractIntegrationTest {

    @Autowired
    private BookingRepository bookingRepository;

    @Test
    @DisplayName("Successfully update booking status by id and Booking Status")
    @Sql(scripts = {
            "classpath:databases/users/add-user-to-users-table.sql",
            "classpath:databases/addresses/add-addresses-to-addresses-table.sql",
            "classpath:databases/accommodations/add-accommodations-to-accommodations-table.sql",
            "classpath:databases/bookings/add-booking-to-bookings-table.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {
            "classpath:databases/bookings/remove-all-from-bookings-table.sql",
            "classpath:databases/accommodations/remove-all-from-accommodations-table.sql",
            "classpath:databases/addresses/remove-all-from-addresses-table.sql",
            "classpath:databases/users/remove-all-from-users-table.sql"
    }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void updateBookingByIdAndStatus_ShouldUpdateBookingStatusById() {
        bookingRepository.updateBookingByIdAndStatus(1L, Booking.Status.CONFIRMED);

        Optional<Booking> actual = bookingRepository.findById(1L);

        assertTrue(actual.isPresent());
        assertEquals("CONFIRMED", actual.get().getStatus().name());
    }
}
