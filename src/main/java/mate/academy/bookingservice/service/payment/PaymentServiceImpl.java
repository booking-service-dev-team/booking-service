package mate.academy.bookingservice.service.payment;

import java.net.MalformedURLException;
import java.util.List;
import com.stripe.exception.StripeException;
import lombok.RequiredArgsConstructor;
import mate.academy.bookingservice.dto.payment.internal.PaymentInfoDto;
import mate.academy.bookingservice.exception.EntityNotFoundException;
import mate.academy.bookingservice.mapper.PaymentMapper;
import mate.academy.bookingservice.model.Booking;
import mate.academy.bookingservice.model.Payment;
import mate.academy.bookingservice.model.User;
import mate.academy.bookingservice.repository.booking.BookingRepository;
import mate.academy.bookingservice.repository.payment.PaymentRepository;
import mate.academy.bookingservice.repository.user.UserRepository;
import mate.academy.bookingservice.service.stripe.StripeService;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
    private final PaymentRepository paymentRepository;
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final PaymentMapper paymentMapper;
    private final StripeService stripeService;
    @Override
    public void initPaymentSession(String userEmail, Long bookingId)
            throws MalformedURLException, StripeException {
        if(!stripeService.doesCustomerExist(userEmail)) {
            User user = userRepository.findByEmail(userEmail).orElseThrow(
                    () -> new EntityNotFoundException("Can't find user by email: " + userEmail)
            );
            stripeService
                    .createCustomer(user.getFirstName() + " " + user.getLastName(), userEmail);
        }

        Booking booking = bookingRepository.findById(bookingId).orElseThrow(
                () -> new EntityNotFoundException("Can't find booking by id: " + bookingId)
        );
        stripeService.createProduct(booking);
    }

    @Override
    public PaymentInfoDto getPaymentInfoDtoByUserId(Long userId) {
        List<Booking> bookingsByUser = bookingRepository
                .getBookingsByUser(userRepository.findById(userId).orElseThrow(
                        () -> new EntityNotFoundException("Can't find user by id: " + userId)
                ));
        // todo znaity vsi payments z ysix bookings
        List<Payment> paymentsByBookingId = paymentRepository.getPaymentsByBookingId(bookingsByUser.get(0).getId());
        return new PaymentInfoDto().setPendingPayments(paymentsByBookingId.stream()
                .map(paymentMapper::toDto)
                .toList());
    }
}
