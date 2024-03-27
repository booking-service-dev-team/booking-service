package mate.academy.bookingservice.service.payment;

import java.util.Map;
import mate.academy.bookingservice.dto.payment.external.PaymentResponseDto;
import mate.academy.bookingservice.dto.payment.internal.PaymentInfoDto;
import mate.academy.bookingservice.model.Payment;
import org.springframework.security.core.Authentication;

public interface PaymentService {

    PaymentInfoDto getPaymentInfoDtoByUserId(Long userId);

    void handlePaymentCancellation(Long paymentId);

    Payment initPayment(Long bookingId, String userEmail);

    PaymentResponseDto handleSuccess(String checkoutSessionId);

    PaymentResponseDto handleCancel(String checkoutSessionId);

    PaymentInfoDto getPaymentInfoDtoByLoggedInUser(Authentication authentication);
}
