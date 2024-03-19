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
import mate.academy.bookingservice.security.JwtUtil;
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
    private final JwtUtil jwtUtil;
    @Value("${payment-endpoint.success.url}") String successEndPointUrl;
    @Value("${payment-endpoint.cancel.url}") String cancelEndPointUrl;
    @Value("${payment-endpoint.query-parameter.payment-id.name}") String paymentIdQueryParameterName;
    @Value("${payment-endpoint.query-parameter.ui-url.name}") String uiUrlQueryParameterName;
    @Override
    public Payment initPayment(String userEmail, String userToken, Long bookingId, String successPaymentUrl, String cancelPaymentUrl)
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
        String stripeToken = jwtUtil.generateToken(userToken);
        Session stripePaymentSession = stripeService.createStripePaymentSession(
                booking.getDescription(),
                booking.getPrice(),
                // todo rewrite with URI builder
                new URL (successEndPointUrl + "?" + paymentIdQueryParameterName + "=" + payment.getId()
                        + "&" + uiUrlQueryParameterName + "=" + URLEncoder.encode(successPaymentUrl, StandardCharsets.UTF_8)
                        + "&token=" + stripeToken),
                new URL (cancelEndPointUrl + "?" + paymentIdQueryParameterName + "=" + payment.getId()
                        + "&" + uiUrlQueryParameterName + "=" + URLEncoder.encode(cancelPaymentUrl, StandardCharsets.UTF_8)
                        + "&token=" + stripeToken),
                userEmail);
        
        payment.setSessionUrl(new URL(stripePaymentSession.getUrl()));
        payment.setSessionId(stripePaymentSession.getId());

        return paymentRepository.save(payment);
    }

    @Override
    public Payment initPayment(Long bookingId, String userEmail) {
        return null;
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
}
