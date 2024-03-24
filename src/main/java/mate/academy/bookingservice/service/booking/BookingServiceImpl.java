package mate.academy.bookingservice.service.booking;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import mate.academy.bookingservice.dto.booking.external.CreateBookingRequestDto;
import mate.academy.bookingservice.dto.booking.external.StatusBookingRequestDto;
import mate.academy.bookingservice.dto.booking.external.UpdateBookingRequestDto;
import mate.academy.bookingservice.dto.booking.internal.BookingDto;
import mate.academy.bookingservice.exception.EntityNotFoundException;
import mate.academy.bookingservice.exception.IllegalArgumentException;
import mate.academy.bookingservice.exception.InvalidDateException;
import mate.academy.bookingservice.mapper.BookingMapper;
import mate.academy.bookingservice.model.Accommodation;
import mate.academy.bookingservice.model.Booking;
import mate.academy.bookingservice.model.User;
import mate.academy.bookingservice.repository.accommodation.AccommodationRepository;
import mate.academy.bookingservice.repository.booking.BookingRepository;
import mate.academy.bookingservice.repository.user.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final AccommodationRepository accommodationRepository;
    private final UserRepository userRepository;
    private final BookingMapper bookingMapper;

    // todo remove things that do not belong to the functionality of this class
    @SneakyThrows
    @Override
    @Transactional
    public BookingDto createBooking(CreateBookingRequestDto requestDto, Authentication authentication) {
        checkingAvailabilityOfDates(requestDto.getCheckInDate(),
                requestDto.getCheckOutDate(),
                requestDto.getAccommodationId());
        Accommodation accommodation = findAccommodationById(requestDto
                .getAccommodationId(), "create");
        // todo create logic for check availability and reduction availability.
        //  maybe reduction availability should doing with change status on CONFIRMED after user payment)
//        if (accommodation.getNumberOfAvailableAccommodation() > 0) {
//            accommodation.setNumberOfAvailableAccommodation(
//                    accommodation.getNumberOfAvailableAccommodation() - 1
//            );
//        } else {
//            throw new InvalidDateException("Accommodation isn't available");
//        }
        Accommodation savedAccommodation = accommodationRepository.save(accommodation);
        Booking booking = new Booking()
                .setCheckInDate(requestDto.getCheckInDate())
                .setCheckOutDate(requestDto.getCheckOutDate())
                .setAccommodation(savedAccommodation)
                .setUser(getUserByAuthentication(authentication))
                .setStatus(Booking.Status.PENDING);

        Booking savedBooking = bookingRepository.save(booking);
        return bookingMapper.toDto(savedBooking);
    }

    @Override
    @SneakyThrows
    public List<BookingDto> getBookingsByUserIdAndStatus(Long userId, String statusName) {
        return bookingRepository
                .getBookingsByUserAndStatus(findUserById(userId),
                        findBookingStatusValueByStatusName(statusName))
                .stream()
                .map(bookingMapper::toDto)
                .toList();
    }

    @Override
    @SneakyThrows
    public List<BookingDto> getBookingsByStatus(String statusName) {
        return bookingRepository
                .getBookingsByStatus(findBookingStatusValueByStatusName(statusName))
                .stream()
                .map(bookingMapper::toDto)
                .toList();
    }

    @Override
    public List<BookingDto> getAllBookingsOfLoggedInUser(Authentication authentication) {
        List<Booking> bookingsByUser = bookingRepository
                .getBookingsByUser(getUserByAuthentication(authentication));
        return bookingsByUser.stream()
                .map(bookingMapper::toDto)
                .toList();
    }

    @Override
    public BookingDto getById(Long id) {
        Booking booking = bookingRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Can't find booking by id: " + id)
        );
        return bookingMapper.toDto(booking);
    }

    @Override
    @SneakyThrows
    public BookingDto updateBookingById(Long id, UpdateBookingRequestDto requestDto) {
        checkingAvailabilityOfDates(requestDto.getCheckInDate(),
                requestDto.getCheckOutDate(),
                requestDto.getAccommodationId());
        Booking booking = new Booking()
                .setId(id)
                .setCheckInDate(requestDto.getCheckInDate())
                .setCheckOutDate(requestDto.getCheckOutDate())
                .setAccommodation(findAccommodationById(requestDto.getAccommodationId(), "update"))
                .setUser(findUserById(requestDto.getUserId()))
                .setStatus(findBookingStatusValueByStatusName(requestDto
                        .getStatusName()));
        Booking savedUpdatedBooking = bookingRepository.save(booking);
        return bookingMapper.toDto(savedUpdatedBooking);
    }

    @Override
    @SneakyThrows
    public BookingDto updateBookingStatusById(Long id, StatusBookingRequestDto requestDto) {
        Booking booking = bookingRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Can't find booking with id: " + id)
        );
        booking.setStatus(findBookingStatusValueByStatusName(requestDto.statusName()));
        Booking savedUpdatedBooking = bookingRepository.save(booking);
        return bookingMapper.toDto(savedUpdatedBooking);
    }

    @Override
    public void deleteById(Long id) {
        bookingRepository.deleteById(id);
    }

    private Booking.Status findBookingStatusValueByStatusName(String statusName)
            throws IllegalArgumentException {
        if (Arrays.stream(Booking.Status.values())
                .map(String::valueOf)
                .noneMatch(s -> s.equals(statusName.toUpperCase()))) {
            throw new IllegalArgumentException("Incorrect value of booking status: "
                    + statusName
                    + ", use one of: " + Arrays.toString(Booking.Status.values()));
        } else {
            return Booking.Status.valueOf(statusName.toUpperCase());
        }
    }

    private User findUserById(Long id) {
        return userRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Can't find user by id: " + id)
        );
    }

    private Accommodation findAccommodationById(Long id, String operation) {
        return accommodationRepository
                .findById(id).orElseThrow(
                        () -> new EntityNotFoundException("Can't " + operation + " booking. "
                                + "Accommodation with id: " + id
                                + " isn't exist")
                );
    }

    private User getUserByAuthentication(Authentication authentication) {
        return userRepository.findByEmail(authentication.getName()).orElseThrow(
                () -> new EntityNotFoundException("Can't find user by email: "
                        + authentication.getName())
        );
    }

    private void checkingAvailabilityOfDates(
            LocalDate checkIn, LocalDate checkOut, Long accommodationId
    )
            throws InvalidDateException {
        if (checkIn.isAfter(checkOut) || checkIn.isBefore(LocalDate.now())) {
            throw new InvalidDateException("Invalid date range");
        }
        List<Booking> bookings = findBookingsByAccommodationIdAndStatus(accommodationId,
                Booking.Status.CONFIRMED);
        for (Booking booking : bookings) {
            if (isDateInRange(checkIn, booking.getCheckInDate(), booking.getCheckOutDate())) {
                throw new InvalidDateException("This date isn't available: " + checkIn);
            }
            if (isDateInRange(checkOut, booking.getCheckInDate(), booking.getCheckOutDate())) {
                throw new InvalidDateException("This date isn't available: " + checkOut);
            }
            if (isDateInRange(booking.getCheckInDate(), checkIn, checkOut)) {
                throw new InvalidDateException("This dates aren't available: "
                        + booking.getCheckInDate() + " - " + booking.getCheckOutDate());
            }
            if (isDateInRange(booking.getCheckOutDate(), checkIn, checkOut)) {
                throw new InvalidDateException("This dates aren't available: "
                        + booking.getCheckInDate() + " - " + booking.getCheckOutDate());
            }
        }
    }

    private List<Booking> findBookingsByAccommodationIdAndStatus(
            Long accommodationId, Booking.Status status) {
        return bookingRepository
                .findBookingsByAccommodationIdAndStatus(accommodationId, status);
    }

    private boolean isDateInRange(LocalDate dateToCheck, LocalDate startDate, LocalDate endDate) {
        return dateToCheck.isAfter(startDate.minusDays(1))
                && dateToCheck.isBefore(endDate.plusDays(1));
    }
}
