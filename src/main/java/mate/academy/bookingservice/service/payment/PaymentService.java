package mate.academy.bookingservice.service.payment;

import java.net.MalformedURLException;
import com.stripe.exception.StripeException;
import mate.academy.bookingservice.dto.payment.internal.PaymentInfoDto;
import mate.academy.bookingservice.model.Payment;

public interface PaymentService {

    PaymentInfoDto getPaymentInfoDtoByUserId(Long userId);

    String handlePaymentSuccess(Long paymentId, String token);

    void handlePaymentCancellation(Long paymentId);

    Payment initPayment(Long bookingId, String userEmail);
}
