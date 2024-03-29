package mate.academy.bookingservice.service.payment;

import mate.academy.bookingservice.dto.payment.external.PaymentResponseDto;
import mate.academy.bookingservice.dto.payment.internal.PaymentInfoDto;
import mate.academy.bookingservice.model.Payment;
import org.springframework.security.core.Authentication;

public interface PaymentService {

    PaymentInfoDto getPaymentInfoDtoByUserId(Long userId);

    Payment initPayment(Long bookingId, String userEmail);

    PaymentResponseDto handleSuccess(String checkoutSessionId);

    PaymentResponseDto handleCancel(String checkoutSessionId);

    PaymentInfoDto getPaymentInfoDtoByLoggedInUser(Authentication authentication);
}
