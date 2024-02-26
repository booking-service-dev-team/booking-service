package mate.academy.bookingservice.service.payment;

import java.net.MalformedURLException;
import com.stripe.exception.StripeException;
import mate.academy.bookingservice.dto.payment.internal.PaymentInfoDto;

public interface PaymentService {
    void initPaymentSession(String userEmail, Long bookingId) throws MalformedURLException, StripeException;

    PaymentInfoDto getPaymentInfoDtoByUserId(Long userId);
}
