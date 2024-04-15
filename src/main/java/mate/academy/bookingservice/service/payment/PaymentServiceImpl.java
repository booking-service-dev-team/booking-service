package mate.academy.bookingservice.service.payment;

import com.stripe.model.Customer;
import com.stripe.model.checkout.Session;
import java.net.URL;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import mate.academy.bookingservice.dto.payment.external.PaymentResponseDto;
import mate.academy.bookingservice.dto.payment.internal.PaymentInfoDto;
import mate.academy.bookingservice.exception.EntityNotFoundException;
import mate.academy.bookingservice.exception.PaymentException;
import mate.academy.bookingservice.mapper.PaymentMapper;
import mate.academy.bookingservice.model.Booking;
import mate.academy.bookingservice.model.Payment;
import mate.academy.bookingservice.repository.booking.BookingRepository;
import mate.academy.bookingservice.repository.payment.PaymentRepository;
import mate.academy.bookingservice.repository.user.UserRepository;
import mate.academy.bookingservice.service.booking.BookingService;
import mate.academy.bookingservice.service.notification.NotificationService;
import mate.academy.bookingservice.service.stripe.StripeService;
import mate.academy.bookingservice.service.user.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriComponentsBuilder;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
    public static final String CHECKOUT_SESSION_ID_QUERY_PARAM
            = "?session_id={CHECKOUT_SESSION_ID}";
    @Value("${payment-endpoint.success.url}") private String successUrl;
    @Value("${payment-endpoint.cancel.url}") private String cancelUrl;
    private final PaymentRepository paymentRepository;
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final UserService userService;
    private final PaymentMapper paymentMapper;
    private final StripeService stripeService;
    private final BookingService bookingService;
    private final NotificationService notificationService;

    @SneakyThrows
    @Override
    @Transactional
    public Payment initPayment(Long bookingId, String userEmail) {
        Customer customer = stripeService.getCustomerByEmail(userEmail);
        Booking booking = getVerifiedBookingWithPendingStatus(bookingId, userEmail);
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

    @Override
    public PaymentInfoDto getPaymentInfoDtoByLoggedInUser(Authentication authentication) {
        return getPaymentInfoDtoByUserId(userService.getUserInfo(authentication).getId());
    }

    @Override
    public PaymentResponseDto handleSuccess(String checkoutSessionId) {
        Map<String, String> paymentData = stripeService
                .getPaymentDataByCheckoutSessionId(checkoutSessionId);
        paymentData.put("message", "Payment success!");
        Long paymentId = Long.valueOf(paymentData.get("paymentId"));
        paymentRepository.updatePaymentStatusById(paymentId, Payment.Status.PAID);
        Long bookingId = paymentRepository.getBookingIdByPaymentId(paymentId);
        bookingRepository.updateBookingByIdAndStatus(bookingId, Booking.Status.CONFIRMED);
        notificationService.sendMessageToAdmins(createMessageByMap(paymentData));
        return buildPaymentResponseDto(paymentData);
    }

    @Override
    public PaymentResponseDto handleCancel(String checkoutSessionId) {
        Map<String, String> paymentData = stripeService
                .getPaymentDataByCheckoutSessionId(checkoutSessionId);
        paymentData.put("message", "The payment is not successful. Link is valid for 24 hours!");
        Long paymentId = Long.valueOf(paymentData.get("paymentId"));
        paymentRepository.updatePaymentStatusById(paymentId, Payment.Status.CANCELED);
        Long bookingId = paymentRepository.getBookingIdByPaymentId(paymentId);
        bookingRepository.updateBookingByIdAndStatus(bookingId, Booking.Status.CANCELED);
        return buildPaymentResponseDto(paymentData);
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

    private Booking getVerifiedBookingWithPendingStatus(Long bookingId, String userEmail) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(
                () -> new EntityNotFoundException("Can't find booking by id: " + bookingId)
        );
        if (!booking.getUser().getEmail().equals(userEmail)) {
            throw new PaymentException("Access denied! Booking doesn't belong to the user");
        }
        if (!booking.getStatus().equals(Booking.Status.PENDING)) {
            throw new PaymentException(
                    "This booking with id: " + bookingId + " have status: " + booking.getStatus()
            );
        }
        bookingService.checkingAvailabilityOfDates(
                booking.getCheckInDate(),
                booking.getCheckOutDate(),
                booking.getAccommodation().getId()
        );
        bookingService.checkAvailabilityOfAccommodation(booking.getAccommodation());
        return booking;
    }

    private PaymentResponseDto buildPaymentResponseDto(Map<String, String> paymentData) {
        return new PaymentResponseDto(
                paymentData.get("message"),
                paymentData.get("paymentId"),
                paymentData.get("productName"),
                paymentData.get("customerName")
        );
    }

    private String createMessageByMap(Map<String, String> paymentData) {
        StringBuilder builder = new StringBuilder();
        paymentData.forEach((key, value) -> builder
                .append(key)
                .append(": ")
                .append(value)
                .append(System.lineSeparator()));
        return builder.toString();
    }
}
