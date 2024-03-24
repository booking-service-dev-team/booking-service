package mate.academy.bookingservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import mate.academy.bookingservice.dto.booking.external.CreateBookingRequestDto;
import mate.academy.bookingservice.dto.booking.external.StatusBookingRequestDto;
import mate.academy.bookingservice.dto.booking.external.UpdateBookingRequestDto;
import mate.academy.bookingservice.dto.booking.internal.BookingDto;
import mate.academy.bookingservice.service.booking.BookingService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Booking management",
        description = "Endpoints for managing bookings.")
@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create new booking",
            description = "Create new booking")
    public BookingDto createBooking(
            @RequestBody @Valid CreateBookingRequestDto requestDto,
            Authentication authentication
    ) {
        return bookingService.createBooking(requestDto, authentication);
    }

    @GetMapping("/")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get a bookings by user id and/or booking statusName",
            description = "Allows a user with administrator rights to retrieve "
                    + "bookings by user ID and specified status name "
                    + "or only by status name")
    public List<BookingDto> getBookingsByUserIdAndStatus(
            @RequestParam(name = "user_id", required = false) Long userId,
            @RequestParam(name = "status") String statusName) {
        if (userId == null) {
            return bookingService.getBookingsByStatus(statusName);
        } else {
            return bookingService.getBookingsByUserIdAndStatus(userId, statusName);
        }
    }

    @GetMapping("/my")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get all bookings of the logged-in user",
            description = "Get all bookings of the logged-in user")
    public List<BookingDto> getAllBookingsOfLoggedInUser(Authentication authentication) {
        return bookingService.getAllBookingsOfLoggedInUser(authentication);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get a booking by id",
            description = "Get a booking by id")
    public BookingDto getBookingById(@PathVariable Long id) {
        return bookingService.getById(id);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Update a booking by id",
            description = "Update a booking by id")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public BookingDto updateBookingById(@PathVariable Long id,
                                        @RequestBody UpdateBookingRequestDto requestDto) {
        return bookingService.updateBookingById(id, requestDto);
    }

    @PatchMapping("/{id}/status")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Update a status of booking by id",
            description = "Allows you to update only the status")
    public BookingDto updateStatus(@PathVariable Long id,
                                   @RequestBody StatusBookingRequestDto requestDto) {
        return bookingService.updateBookingStatusById(id, requestDto);
    }

    @Operation(summary = "Delete a booking by id",
            description = "Soft delete a booking by id")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable Long id) {
        bookingService.deleteById(id);
    }
}
