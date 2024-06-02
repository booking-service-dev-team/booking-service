package mate.academy.bookingservice.service.booking;

import java.time.LocalDate;
import java.util.List;
import mate.academy.bookingservice.dto.booking.external.CreateBookingRequestDto;
import mate.academy.bookingservice.dto.booking.external.StatusBookingRequestDto;
import mate.academy.bookingservice.dto.booking.external.UpdateBookingRequestDto;
import mate.academy.bookingservice.dto.booking.internal.BookingDto;
import mate.academy.bookingservice.model.Accommodation;
import mate.academy.bookingservice.model.Booking;
import mate.academy.bookingservice.model.User;
import org.springframework.security.core.Authentication;

public interface BookingService {

    List<BookingDto> getBookingsByUserIdAndStatus(Long userId, String status);

    List<BookingDto> getAllBookingsOfLoggedInUser(Authentication authentication);

    List<BookingDto> getBookingsByStatus(String status);

    List<Booking> getBookingsByUser(User user);

    BookingDto createBooking(CreateBookingRequestDto requestDto, Authentication authentication);

    BookingDto getById(Long id);

    BookingDto updateBookingById(Long id, UpdateBookingRequestDto requestDto);

    BookingDto updateBookingStatusById(Long id, StatusBookingRequestDto requestDto);

    BookingDto cancellationUsersBookingById(Long bookingId, Authentication authentication);

    Booking getVerifiedBookingWithPendingStatus(Long bookingId, String userEmail);

    void deleteById(Long id);

    void checkingAvailabilityOfDates(LocalDate checkIn, LocalDate checkOut, Long accommodationId);

    void checkAvailabilityOfAccommodation(Accommodation accommodation);

    void filterAllExpiredBookings();
}
