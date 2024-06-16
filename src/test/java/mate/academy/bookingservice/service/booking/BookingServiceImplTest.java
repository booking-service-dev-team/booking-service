package mate.academy.bookingservice.service.booking;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import mate.academy.bookingservice.dto.booking.external.CreateBookingRequestDto;
import mate.academy.bookingservice.dto.booking.internal.BookingDto;
import mate.academy.bookingservice.exception.AvailabilityException;
import mate.academy.bookingservice.exception.EntityNotFoundException;
import mate.academy.bookingservice.exception.IllegalArgumentException;
import mate.academy.bookingservice.exception.InvalidDateException;
import mate.academy.bookingservice.exception.PaymentException;
import mate.academy.bookingservice.mapper.BookingMapper;
import mate.academy.bookingservice.model.Accommodation;
import mate.academy.bookingservice.model.Address;
import mate.academy.bookingservice.model.Booking;
import mate.academy.bookingservice.model.Payment;
import mate.academy.bookingservice.model.User;
import mate.academy.bookingservice.repository.accommodation.AccommodationRepository;
import mate.academy.bookingservice.repository.booking.BookingRepository;
import mate.academy.bookingservice.repository.payment.PaymentRepository;
import mate.academy.bookingservice.service.notification.NotificationService;
import mate.academy.bookingservice.service.user.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {

    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private AccommodationRepository accommodationRepository;
    @Mock
    private UserService userService;
    @Mock
    private PaymentRepository paymentRepository;
    @Mock
    private BookingMapper bookingMapper;
    @Mock
    private NotificationService notificationService;
    @Mock
    private Authentication authentication;
    @InjectMocks
    private BookingServiceImpl bookingService;

    @Test
    @DisplayName("Successfully created a new booking")
    void createBooking_ValidRequest_Success() {
        Long userId = 8L;

        User user = new User();
        user.setId(userId);

        Address address = new Address();
        address.setCountryName("England");
        address.setCityName("London");
        address.setStreetName("Baker st.");
        address.setNumberOfHouse("221B");

        Long accommodationId = 1L;

        Accommodation accommodation = new Accommodation();
        accommodation.setId(accommodationId);
        accommodation.setNumberOfAvailableAccommodation(1);
        accommodation.setAddress(address);

        LocalDate checkInDate = LocalDate.now().plusDays(10);
        LocalDate checkOutDate = LocalDate.now().plusDays(20);

        Booking booking = new Booking();
        booking.setCheckInDate(checkInDate);
        booking.setCheckOutDate(checkOutDate);
        booking.setAccommodation(accommodation);
        booking.setUser(user);
        booking.setStatus(Booking.Status.PENDING);

        Long bookingId = 4L;

        Booking savedBooking = new Booking();
        savedBooking.setId(bookingId);
        savedBooking.setCheckInDate(checkInDate);
        savedBooking.setCheckOutDate(checkOutDate);
        savedBooking.setAccommodation(accommodation);
        savedBooking.setUser(user);
        savedBooking.setStatus(Booking.Status.PENDING);

        BookingDto expected = new BookingDto()
                .setId(bookingId)
                .setCheckInDate(checkInDate)
                .setCheckOutDate(checkOutDate)
                .setAccommodationId(accommodationId)
                .setUserId(userId)
                .setStatusName("PENDING");

        when(userService.getUserByAuthentication(authentication)).thenReturn(user);
        when(bookingRepository.getBookingsByUser(user)).thenReturn(new ArrayList<>());
        when(bookingRepository
                .findBookingsByAccommodationIdAndStatus(accommodationId, Booking.Status.CONFIRMED))
                .thenReturn(new ArrayList<>());
        when(accommodationRepository.findById(accommodationId))
                .thenReturn(Optional.of(accommodation));
        when(bookingRepository.save(booking)).thenReturn(savedBooking);
        when(bookingMapper.toDto(savedBooking)).thenReturn(expected);

        CreateBookingRequestDto requestDto = new CreateBookingRequestDto()
                .setCheckInDate(checkInDate)
                .setCheckOutDate(checkOutDate)
                .setAccommodationId(accommodationId);

        BookingDto actual = bookingService.createBooking(requestDto, authentication);

        assertNotNull(actual);
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Unsuccessful attempt to create a new booking "
                    + "when the check-in date is after check-out")
    void createBooking_WhenCheckInIsAfterCheckOut_NotSuccess() {
        Long userId = 8L;

        User user = new User();
        user.setId(userId);

        when(userService.getUserByAuthentication(authentication)).thenReturn(user);
        when(bookingRepository.getBookingsByUser(user)).thenReturn(new ArrayList<>());

        CreateBookingRequestDto requestDto = new CreateBookingRequestDto()
                .setCheckInDate(LocalDate.now().plusDays(10))
                .setCheckOutDate(LocalDate.now().plusDays(5))
                .setAccommodationId(1L);

        assertThrows(InvalidDateException.class, () -> {
            bookingService.createBooking(requestDto, authentication);
        });
    }

    @Test
    @DisplayName("Unsuccessful attempt to create a new booking "
            + "when the check-in date is before now")
    void createBooking_WhenCheckInIsBeforeNow_NotSuccess() {
        Long userId = 8L;

        User user = new User();
        user.setId(userId);

        when(userService.getUserByAuthentication(authentication)).thenReturn(user);
        when(bookingRepository.getBookingsByUser(user)).thenReturn(new ArrayList<>());

        CreateBookingRequestDto requestDto = new CreateBookingRequestDto()
                .setCheckInDate(LocalDate.now().minusDays(3))
                .setCheckOutDate(LocalDate.now().plusDays(5))
                .setAccommodationId(1L);

        assertThrows(InvalidDateException.class, () -> {
            bookingService.createBooking(requestDto, authentication);
        });
    }

    @Test
    @DisplayName("Unsuccessful attempt to create a new booking "
            + "when the check-in date is not available for specified accommodation")
    void createBooking_WhenCheckInIsNotAvailable_NotSuccess() {
        LocalDate bookingCheckInDate = LocalDate.now().plusDays(9);
        LocalDate checkInDate = LocalDate.now().plusDays(10);
        LocalDate bookingCheckOutDate = LocalDate.now().plusDays(11);

        LocalDate checkOutDate = LocalDate.now().plusDays(25);

        datesChecker(checkInDate, checkOutDate, bookingCheckInDate, bookingCheckOutDate);
    }

    @Test
    @DisplayName("Unsuccessful attempt to create a new booking "
            + "when the check-out date is not available for specified accommodation")
    void createBooking_WhenCheckOutIsNotAvailable_NotSuccess() {
        LocalDate checkInDate = LocalDate.now().plusDays(5);

        LocalDate bookingCheckInDate = LocalDate.now().plusDays(9);
        LocalDate checkOutDate = LocalDate.now().plusDays(10);
        LocalDate bookingCheckOutDate = LocalDate.now().plusDays(11);

        datesChecker(checkInDate, checkOutDate, bookingCheckInDate, bookingCheckOutDate);
    }

    @Test
    @DisplayName("Unsuccessful attempt to create a new booking "
            + "when range of dates inside available booking dates for specified accommodation")
    void createBooking_WhenDatesInsideAvailableBookingDates_NotSuccess() {
        LocalDate bookingCheckInDate = LocalDate.now().plusDays(2);
        LocalDate checkInDate = LocalDate.now().plusDays(5);
        LocalDate checkOutDate = LocalDate.now().plusDays(10);
        LocalDate bookingCheckOutDate = LocalDate.now().plusDays(15);

        datesChecker(checkInDate, checkOutDate, bookingCheckInDate, bookingCheckOutDate);
    }

    @Test
    @DisplayName("Unsuccessful attempt to create a new booking when confirmed booking dates"
            + " inside the date range for specified accommodation")
    void createBooking_WhenConfirmedBookingDatesInsideDates_NotSuccess() {
        LocalDate checkInDate = LocalDate.now().plusDays(5);
        LocalDate bookingCheckInDate = LocalDate.now().plusDays(10);
        LocalDate bookingCheckOutDate = LocalDate.now().plusDays(15);
        LocalDate checkOutDate = LocalDate.now().plusDays(25);

        datesChecker(checkInDate, checkOutDate, bookingCheckInDate, bookingCheckOutDate);
    }

    @Test
    @DisplayName("Unsuccessful attempt to create a new booking when user have pending payment")
    void createBooking_UserHavePendingPayment_NotSuccess() {
        Long userId = 8L;
        Long bookingId = 4L;

        User user = new User();
        user.setId(userId);

        Booking booking = new Booking();
        booking.setId(bookingId);

        Payment payment = new Payment();
        payment.setStatus(Payment.Status.PENDING);

        when(userService.getUserByAuthentication(authentication)).thenReturn(user);
        when(bookingRepository.getBookingsByUser(user)).thenReturn(List.of(booking));
        when(paymentRepository.getPaymentsByBookingId(booking.getId()))
                .thenReturn(List.of(payment));

        CreateBookingRequestDto requestDto = new CreateBookingRequestDto()
                .setCheckInDate(LocalDate.now().plusDays(10))
                .setCheckOutDate(LocalDate.now().plusDays(20))
                .setAccommodationId(1L);

        assertThrows(PaymentException.class, () -> {
            bookingService.createBooking(requestDto, authentication);
        });
    }

    @Test
    @DisplayName("Unsuccessful attempt to create a new booking in non available accommodation")
    void createBooking_NonAvailableAccommodation_NotSuccess() {
        Long userId = 8L;

        User user = new User();
        user.setId(userId);

        Address address = new Address();
        address.setCountryName("England");
        address.setCityName("London");
        address.setStreetName("Baker st.");
        address.setNumberOfHouse("221B");

        Long accommodationId = 1L;

        Accommodation accommodation = new Accommodation();
        accommodation.setId(accommodationId);
        accommodation.setNumberOfAvailableAccommodation(0);
        accommodation.setAddress(address);

        when(userService.getUserByAuthentication(authentication)).thenReturn(user);
        when(bookingRepository.getBookingsByUser(user)).thenReturn(new ArrayList<>());
        when(bookingRepository
                .findBookingsByAccommodationIdAndStatus(accommodationId, Booking.Status.CONFIRMED))
                .thenReturn(new ArrayList<>());
        when(accommodationRepository.findById(accommodationId))
                .thenReturn(Optional.of(accommodation));

        CreateBookingRequestDto requestDto = new CreateBookingRequestDto()
                .setCheckInDate(LocalDate.now().plusDays(10))
                .setCheckOutDate(LocalDate.now().plusDays(20))
                .setAccommodationId(accommodationId);
        assertThrows(AvailabilityException.class, () -> {
            bookingService.createBooking(requestDto, authentication);
        });
    }

    @Test
    @DisplayName("Unsuccessful attempt to create a new booking for non-existing accommodation")
    void createBooking_NonExistingAccommodation_NotSuccess() {
        Long userId = 8L;

        User user = new User();
        user.setId(userId);

        Address address = new Address();
        address.setCountryName("England");
        address.setCityName("London");
        address.setStreetName("Baker st.");
        address.setNumberOfHouse("221B");

        Long accommodationId = 1L;

        Accommodation accommodation = new Accommodation();
        accommodation.setId(accommodationId);
        accommodation.setNumberOfAvailableAccommodation(0);
        accommodation.setAddress(address);

        when(userService.getUserByAuthentication(authentication)).thenReturn(user);
        when(bookingRepository.getBookingsByUser(user)).thenReturn(new ArrayList<>());
        when(bookingRepository
                .findBookingsByAccommodationIdAndStatus(accommodationId, Booking.Status.CONFIRMED))
                .thenReturn(new ArrayList<>());
        when(accommodationRepository.findById(accommodationId)).thenReturn(Optional.empty());

        CreateBookingRequestDto requestDto = new CreateBookingRequestDto()
                .setCheckInDate(LocalDate.now().plusDays(10))
                .setCheckOutDate(LocalDate.now().plusDays(20))
                .setAccommodationId(accommodationId);

        assertThrows(EntityNotFoundException.class, () -> {
            bookingService.createBooking(requestDto, authentication);
        });
    }

    @Test
    @DisplayName("Successful cancellation of booking")
    void cancelUsersBookingById_ByValidId_Success() {
        Long userId = 5L;

        User user = new User();
        user.setId(userId);

        Address address = new Address();
        address.setCountryName("England");
        address.setCityName("London");
        address.setStreetName("Baker st.");
        address.setNumberOfHouse("221B");

        Long accommodationId = 9L;

        Accommodation accommodation = new Accommodation();
        accommodation.setId(accommodationId);
        accommodation.setNumberOfAvailableAccommodation(1);
        accommodation.setAddress(address);

        Long bookingId = 1L;

        Booking booking = new Booking();
        booking.setId(bookingId);
        booking.setAccommodation(accommodation);
        booking.setStatus(Booking.Status.PENDING);

        Booking canceledBooking = new Booking();
        canceledBooking.setId(bookingId);
        canceledBooking.setAccommodation(accommodation);
        canceledBooking.setStatus(Booking.Status.CANCELED);

        BookingDto expected = new BookingDto()
                .setUserId(canceledBooking.getId())
                .setAccommodationId(canceledBooking.getAccommodation().getId())
                .setStatusName("CANCELED");

        when(userService.getUserByAuthentication(authentication)).thenReturn(user);
        when(bookingRepository.getBookingsByUser(user)).thenReturn(List.of(booking));
        when(bookingRepository.save(canceledBooking)).thenReturn(canceledBooking);
        when(bookingMapper.toDto(canceledBooking)).thenReturn(expected);

        BookingDto actual = bookingService.cancellationUsersBookingById(bookingId, authentication);

        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Unsuccessful cancellation when relevant user's booking is not found")
    void cancelUsersBookingById_WithNoPendingBookings_NotSuccess() {
        Long userId = 5L;

        User user = new User();
        user.setId(userId);

        Address address = new Address();
        address.setCountryName("England");
        address.setCityName("London");
        address.setStreetName("Baker st.");
        address.setNumberOfHouse("221B");

        Accommodation accommodation = new Accommodation();
        accommodation.setId(9L);
        accommodation.setNumberOfAvailableAccommodation(1);
        accommodation.setAddress(address);

        Long bookingId = 1L;

        Booking booking = new Booking();
        booking.setId(bookingId);
        booking.setAccommodation(accommodation);
        booking.setStatus(Booking.Status.CONFIRMED);

        when(userService.getUserByAuthentication(authentication)).thenReturn(user);
        when(bookingRepository.getBookingsByUser(user)).thenReturn(List.of(booking));

        assertThrows(EntityNotFoundException.class, () -> {
            bookingService.cancellationUsersBookingById(bookingId, authentication);
        });
    }

    @Test
    @DisplayName("Successfully getting bookings by user-id and name of status")
    void getBookingsByUserIdAndStatus_ByValidUserIdAndStatusName_Success() {
        Long userId = 1L;

        User user = new User();
        user.setId(userId);

        Booking booking = new Booking();
        booking.setUser(user);
        booking.setStatus(Booking.Status.PENDING);

        String statusName = "PENDING";

        BookingDto bookingDto = new BookingDto()
                .setUserId(userId)
                .setStatusName(statusName);

        when(userService.findUserById(userId)).thenReturn(user);
        when(bookingRepository.getBookingsByUserAndStatus(user, Booking.Status.PENDING))
                .thenReturn(List.of(booking));
        when(bookingMapper.toDto(booking)).thenReturn(bookingDto);

        List<BookingDto> actual = bookingService.getBookingsByUserIdAndStatus(userId, statusName);
        List<BookingDto> expected = List.of(bookingDto);

        assertNotNull(actual);
        assertFalse(actual.isEmpty());
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Unsuccessful getting bookings by non-existing name of status")
    void getBookingsByUserIdAndStatus_ByNotValidStatusName_NotSuccess() {
        Long userId = 1L;
        String statusName = "NON-EXISTING";

        User user = new User();
        user.setId(userId);

        when(userService.findUserById(userId)).thenReturn(user);

        assertThrows(IllegalArgumentException.class, () -> {
            bookingService.getBookingsByUserIdAndStatus(userId, statusName);
        });
    }

    @Test
    @DisplayName("Successful getting bookings by name of status")
    void getBookingsByStatus_ValidStatusName_Success() {
        String statusName = "Confirmed";

        Booking booking = new Booking();
        booking.setStatus(Booking.Status.CONFIRMED);

        BookingDto bookingDto = new BookingDto()
                .setStatusName(statusName);

        when(bookingRepository.getBookingsByStatus(Booking.Status.CONFIRMED))
                .thenReturn(List.of(booking));
        when(bookingMapper.toDto(booking)).thenReturn(bookingDto.setStatusName(statusName));

        List<BookingDto> actual = bookingService.getBookingsByStatus(statusName);
        List<BookingDto> expected = List.of(bookingDto);

        assertNotNull(actual);
        assertFalse(actual.isEmpty());
        assertEquals(expected.get(0).getStatusName(), actual.get(0).getStatusName());
    }

    private void datesChecker(
            LocalDate checkInDate,
            LocalDate checkOutDate,
            LocalDate bookingCheckInDate,
            LocalDate bookingCheckOutDate
    ) {
        User user = new User();
        user.setId(8L);

        Address address = new Address();
        address.setCountryName("England");
        address.setCityName("London");
        address.setStreetName("Baker st.");
        address.setNumberOfHouse("221B");

        Long accommodationId = 1L;

        Accommodation accommodation = new Accommodation();
        accommodation.setId(accommodationId);
        accommodation.setNumberOfAvailableAccommodation(1);
        accommodation.setAddress(address);

        Booking booking = new Booking();
        booking.setId(13L);
        booking.setCheckInDate(bookingCheckInDate);
        booking.setCheckOutDate(bookingCheckOutDate);
        booking.setAccommodation(accommodation);
        booking.setUser(user);
        booking.setStatus(Booking.Status.CONFIRMED);

        when(userService.getUserByAuthentication(authentication)).thenReturn(user);
        when(bookingRepository.getBookingsByUser(user)).thenReturn(new ArrayList<>());
        when(bookingRepository
                .findBookingsByAccommodationIdAndStatus(accommodationId, Booking.Status.CONFIRMED))
                .thenReturn(List.of(booking));

        CreateBookingRequestDto requestDto = new CreateBookingRequestDto()
                .setCheckInDate(checkInDate)
                .setCheckOutDate(checkOutDate)
                .setAccommodationId(accommodationId);

        assertThrows(InvalidDateException.class, () -> {
            bookingService.createBooking(requestDto, authentication);
        });
    }
}
