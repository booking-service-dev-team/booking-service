package mate.academy.bookingservice.service.payment;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import mate.academy.bookingservice.dto.payment.external.PaymentResponseDto;
import mate.academy.bookingservice.dto.payment.internal.PaymentDto;
import mate.academy.bookingservice.dto.payment.internal.PaymentInfoDto;
import mate.academy.bookingservice.mapper.PaymentMapper;
import mate.academy.bookingservice.model.Booking;
import mate.academy.bookingservice.model.Payment;
import mate.academy.bookingservice.model.User;
import mate.academy.bookingservice.repository.booking.BookingRepository;
import mate.academy.bookingservice.repository.payment.PaymentRepository;
import mate.academy.bookingservice.service.booking.BookingService;
import mate.academy.bookingservice.service.notification.NotificationService;
import mate.academy.bookingservice.service.stripe.StripeService;
import mate.academy.bookingservice.service.user.UserService;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PaymentServiceImplTest {

    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private BookingService bookingService;
    @Mock
    private UserService userService;
    @Mock
    private PaymentRepository paymentRepository;
    @Mock
    private PaymentMapper paymentMapper;
    @Mock
    private StripeService stripeService;
    @Mock
    private NotificationService notificationService;
    @InjectMocks
    private PaymentServiceImpl paymentService;

    @Test
    @DisplayName("Successfully getting list of user payments by user id")
    void getPaymentInfoDtoByUserId_ByValidId_Success() {
        Long userId = 1L;
        Long bookingId = 5L;

        User user = new User();
        user.setId(userId);

        List<Booking> bookings = List.of(new Booking()
                .setId(bookingId)
                .setUser(user));

        List<Payment> payments = List.of(new Payment()
                .setBookingId(bookingId));

        PaymentDto paymentDto = new PaymentDto()
                .setBookingId(bookingId);

        when(userService.findUserById(userId)).thenReturn(user);
        when(bookingService.getBookingsByUser(user)).thenReturn(bookings);
        when(paymentRepository.getPaymentsByBookingId(bookingId)).thenReturn(payments);
        when(paymentMapper.toDto(payments.get(0))).thenReturn(paymentDto);

        PaymentInfoDto actual = paymentService.getPaymentInfoDtoByUserId(userId);

        PaymentInfoDto expected = new PaymentInfoDto()
                .setPaymentDtos(List.of(paymentDto));

        assertNotNull(actual);
        assertEquals(expected.getPaymentDtos().size(), actual.getPaymentDtos().size());
        assertEquals(expected.getPaymentDtos().get(0).getBookingId(),
                actual.getPaymentDtos().get(0).getBookingId());
    }

    @Test
    @DisplayName("Successful handling of successful payment")
    void handleSuccess_ByValidCheckoutSessionId_Success() {
        var paymentData = new HashMap<>(getPaymentData());

        Long bookingId = 5L;

        when(stripeService.getPaymentDataByCheckoutSessionId(anyString()))
                .thenReturn(paymentData);
        when(paymentRepository.getBookingIdByPaymentId(anyLong())).thenReturn(bookingId);

        PaymentResponseDto expected = new PaymentResponseDto(
                "Payment success!",
                paymentData.get("paymentId"),
                paymentData.get("productName"),
                paymentData.get("customerName")
        );

        PaymentResponseDto actual = paymentService.handleSuccess(anyString());

        assertNotNull(actual);
        assertTrue(EqualsBuilder.reflectionEquals(expected, actual));
    }

    @Test
    @DisplayName("Successful handling of unsuccessful payment")
    void handleCancel_ByValidCheckoutSessionId_Success() {
        var paymentData = new HashMap<>(getPaymentData());

        Long bookingId = 5L;

        when(stripeService.getPaymentDataByCheckoutSessionId(anyString()))
                .thenReturn(paymentData);
        when(paymentRepository.getBookingIdByPaymentId(anyLong())).thenReturn(bookingId);

        PaymentResponseDto expected = new PaymentResponseDto(
                "The payment is not successful. Link is valid for 24 hours!",
                paymentData.get("paymentId"),
                paymentData.get("productName"),
                paymentData.get("customerName")
        );

        PaymentResponseDto actual = paymentService.handleCancel(anyString());

        assertNotNull(actual);
        assertTrue(EqualsBuilder.reflectionEquals(expected, actual));
    }

    private Map<String, String> getPaymentData() {
        return Map.of(
                "paymentId", "123",
                "productName", "product",
                "customerName", "customer");
    }
}
