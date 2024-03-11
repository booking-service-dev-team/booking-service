package mate.academy.bookingservice.service.payment;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
    private final PaymentRepository paymentRepository;
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final PaymentMapper paymentMapper;
    private final StripeService stripeService;
    @Value("${payment-endpoint.success.url}") String successEndPointUrl;
    @Value("${payment-endpoint.cancel.url}") String cancelEndPointUrl;
    @Value("${payment-endpoint.query-parameter.payment-id.name}") String paymentIdQueryParameterName;
    @Value("${payment-endpoint.query-parameter.ui-url.name}") String uiUrlQueryParameterName;
    @Override
    public Payment initPayment(String userEmail, Long bookingId, String successPaymentUrl, String cancelPaymentUrl)
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
        Payment payment = new Payment()
                .setStatus(Payment.Status.PENDING)
                .setBookingId(booking.getId())
                .setAmountToPayUsd(booking.getPrice());
        payment = paymentRepository.save(payment);

        Session paymentSession = stripeService.createPaymentSession(
                booking.getDescription(),
                booking.getPrice(),
                // todo rewrite with URI builder
                new URL (successEndPointUrl + "?" + paymentIdQueryParameterName + "=" + payment.getId()
                        + "&" + uiUrlQueryParameterName + "=" + URLEncoder.encode(successPaymentUrl, StandardCharsets.UTF_8)),
                new URL (cancelEndPointUrl + "?" + paymentIdQueryParameterName + "=" + payment.getId()
                        + "&" + uiUrlQueryParameterName + "=" + URLEncoder.encode(cancelPaymentUrl, StandardCharsets.UTF_8)),
                userEmail);

        payment.setSessionUrl(new URL(paymentSession.getUrl()));
        payment.setSessionId(paymentSession.getId());

        return paymentRepository.save(payment);
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

    @Override
    public void handlePaymentSuccess(Long paymentId) {
        paymentRepository.updatePaymentStatusById(paymentId, Payment.Status.PAID);
    }

    @Override
    public void handlePaymentCancellation(Long paymentId) {
        paymentRepository.updatePaymentStatusById(paymentId, Payment.Status.CANCELED);
    }
}
