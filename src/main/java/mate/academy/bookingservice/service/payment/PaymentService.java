package mate.academy.bookingservice.service.payment;

import java.net.MalformedURLException;
import com.stripe.exception.StripeException;
import mate.academy.bookingservice.dto.payment.internal.PaymentInfoDto;
import mate.academy.bookingservice.model.Payment;

public interface PaymentService {
    Payment initPayment(String userEmail, String userToken, Long bookingId, String successPaymentUrl, String cancelPaymentUrl) throws MalformedURLException, StripeException;

    PaymentInfoDto getPaymentInfoDtoByUserId(Long userId);

    String handlePaymentSuccess(Long paymentId, String token);

    void handlePaymentCancellation(Long paymentId);
}
