package mate.academy.bookingservice.service.payment;

import java.net.URL;
import java.util.List;
import com.stripe.model.Customer;
import com.stripe.model.checkout.Session;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import mate.academy.bookingservice.dto.payment.internal.PaymentInfoDto;
import mate.academy.bookingservice.exception.EntityNotFoundException;
import mate.academy.bookingservice.mapper.PaymentMapper;
import mate.academy.bookingservice.model.Booking;
import mate.academy.bookingservice.model.Payment;
import mate.academy.bookingservice.model.User;
import mate.academy.bookingservice.repository.booking.BookingRepository;
import mate.academy.bookingservice.repository.payment.PaymentRepository;
import mate.academy.bookingservice.repository.user.UserRepository;
import mate.academy.bookingservice.security.JwtUtil;
import mate.academy.bookingservice.service.stripe.StripeService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

@Component
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
    private final JwtUtil jwtUtil;

    @SneakyThrows
    @Override
    public Payment initPayment(Long bookingId, String userEmail) {
        Customer customer = stripeService.getCustomerByEmail(userEmail);
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(
                () -> new EntityNotFoundException("Can't find booking by id: " + bookingId)
        );
        Payment payment = new Payment()
                .setStatus(Payment.Status.PENDING)
                .setBookingId(booking.getId())
                .setAmountToPayUsd(booking.getPrice());
        payment = paymentRepository.save(payment);

        Session stripePaymentSession = stripeService.createStripePaymentSession(
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
    public String handlePaymentSuccess(Long paymentId, String token) {
        String originToken = jwtUtil.getSubject(token);
        String userEmail = jwtUtil.getSubject(originToken);
        User userByPaymentId = getUserByPaymentId(paymentId);
        // todo get user by payment id
        // todo compare useremail with useremail from origin token  & thw secur exp if not eql
        paymentRepository.updatePaymentStatusById(paymentId, Payment.Status.PAID);
        // todo handle change booking status
        return originToken;
    }

    private User getUserByPaymentId(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId).get();
        Booking booking = bookingRepository.findById(payment.getBookingId()).get();
        return booking.getUser();
    }

    @Override
    public void handlePaymentCancellation(Long paymentId) {
        paymentRepository.updatePaymentStatusById(paymentId, Payment.Status.CANCELED);
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
}
