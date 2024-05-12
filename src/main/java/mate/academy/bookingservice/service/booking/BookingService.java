package mate.academy.bookingservice.service.booking;

import java.time.LocalDate;
import java.util.List;
import mate.academy.bookingservice.dto.booking.external.CreateBookingRequestDto;
import mate.academy.bookingservice.dto.booking.external.StatusBookingRequestDto;
import mate.academy.bookingservice.dto.booking.external.UpdateBookingRequestDto;
import mate.academy.bookingservice.dto.booking.internal.BookingDto;
import mate.academy.bookingservice.model.Accommodation;
import org.springframework.security.core.Authentication;

public interface BookingService {
    BookingDto createBooking(CreateBookingRequestDto requestDto, Authentication authentication);

    List<BookingDto> getBookingsByUserIdAndStatus(Long userId, String status);

    List<BookingDto> getAllBookingsOfLoggedInUser(Authentication authentication);

    BookingDto getById(Long id);

    BookingDto updateBookingById(Long id, UpdateBookingRequestDto requestDto);

    BookingDto updateBookingStatusById(Long id, StatusBookingRequestDto requestDto);

    void deleteById(Long id);

    List<BookingDto> getBookingsByStatus(String status);

    BookingDto cancelUsersBookingById(Long bookingId, Authentication authentication);

    void checkingAvailabilityOfDates(
            LocalDate checkIn, LocalDate checkOut, Long accommodationId
    );

    void checkAvailabilityOfAccommodation(Accommodation accommodation);

    void filterAllExpiredBookings();
}
