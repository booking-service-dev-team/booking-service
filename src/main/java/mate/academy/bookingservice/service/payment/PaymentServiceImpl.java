package mate.academy.bookingservice.service.payment;

import java.net.URL;
import java.util.List;
import java.util.Map;
import com.stripe.model.Customer;
import com.stripe.model.checkout.Session;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import mate.academy.bookingservice.dto.payment.external.PaymentResultDto;
import mate.academy.bookingservice.dto.payment.internal.PaymentInfoDto;
import mate.academy.bookingservice.exception.EntityNotFoundException;
import mate.academy.bookingservice.exception.PaymentException;
import mate.academy.bookingservice.mapper.PaymentMapper;
import mate.academy.bookingservice.model.Booking;
import mate.academy.bookingservice.model.Payment;
import mate.academy.bookingservice.model.User;
import mate.academy.bookingservice.repository.booking.BookingRepository;
import mate.academy.bookingservice.repository.payment.PaymentRepository;
import mate.academy.bookingservice.repository.user.UserRepository;
import mate.academy.bookingservice.service.stripe.StripeService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriComponentsBuilder;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
    @Value("${payment-endpoint.success.url}") String successUrl;
    @Value("${payment-endpoint.cancel.url}") String cancelUrl;
    public static final String CHECKOUT_SESSION_ID_QUERY_PARAM
            = "?session_id={CHECKOUT_SESSION_ID}";
    private final PaymentRepository paymentRepository;
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final PaymentMapper paymentMapper;
    private final StripeService stripeService;

    @SneakyThrows
    @Override
    @Transactional
    public Payment initPayment(Long bookingId, String userEmail) {
        Customer customer = stripeService.getCustomerByEmail(userEmail);
        Booking booking = getVerifiedBookingWithPendingStatus(bookingId);
        Payment payment = new Payment()
                .setStatus(Payment.Status.PENDING)
                .setBookingId(booking.getId())
                .setAmountToPayUsd(booking.getPrice());
        payment = paymentRepository.save(payment);

        Session stripePaymentSession = stripeService.createStripePaymentSession(
                payment.getId(),
                booking.getDescription(),
                booking.getPrice(),
                createUrl(successUrl),
                createUrl(cancelUrl),
                customer.getId()
        );

        payment.setSessionUrl(new URL(stripePaymentSession.getUrl()));
        payment.setSessionId(stripePaymentSession.getId());
        return paymentRepository.save(payment);
    }

    @Override
    public PaymentInfoDto getPaymentInfoDtoByUserId(Long userId) {
        List<Payment> paymentsByUserId = getPaymentsByUserId(userId);
        return new PaymentInfoDto().setPaymentDtos(paymentsByUserId.stream()
                .map(paymentMapper::toDto)
                .toList());
    }

    private User getUserByPaymentId(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId).get();
        Booking booking = bookingRepository.findById(payment.getBookingId()).get();
        return booking.getUser();
    }

    @Override
    public void handlePaymentCancellation(Long paymentId) {
//        paymentRepository.updatePaymentByIdAndStatus(paymentId, Payment.Status.CANCELED);
    }

    @Override
    public PaymentResultDto handleSuccess(String checkoutSessionId) {
        Map<String, String> paymentData = stripeService
                .getPaymentDataByCheckoutSessionId(checkoutSessionId);
        Long paymentId = Long.valueOf(paymentData.get("paymentId"));
        paymentRepository.updatePaymentStatusById(paymentId, Payment.Status.PAID);
        Long bookingId = paymentRepository.getBookingIdByPaymentId(paymentId);
        bookingRepository.updateBookingByIdAndStatus(bookingId, Booking.Status.CONFIRMED);
        return new PaymentResultDto(
                paymentData.get("paymentId"),
                paymentData.get("productName"),
                paymentData.get("customerName")
        );
    }

    private List<Payment> getPaymentsByUserId(Long userId) {
        List<Booking> bookingsByUser = bookingRepository
                .getBookingsByUser(userRepository.findById(userId).orElseThrow(
                        () -> new EntityNotFoundException("Can't find user by id: " + userId)
                ));
        return bookingsByUser.stream()
                .map(booking -> paymentRepository.getPaymentsByBookingId(booking.getId()))
                .flatMap(List<Payment>::stream)
                .toList();
    }

    private String createUrl(String url) {
        return UriComponentsBuilder
                .fromHttpUrl(url)
                .toUriString()
                .concat(CHECKOUT_SESSION_ID_QUERY_PARAM);
    }

    private Booking getVerifiedBookingWithPendingStatus(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(
                () -> new EntityNotFoundException("Can't find booking by id: " + bookingId)
        );
        if (booking.getStatus() != Booking.Status.PENDING) {
            throw new PaymentException(
                    "This booking with id: " + bookingId + " have status: " + booking.getStatus()
            );
        }
        return booking;
    }
}
