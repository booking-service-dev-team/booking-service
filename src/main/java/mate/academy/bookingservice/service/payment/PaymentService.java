package mate.academy.bookingservice.service.payment;

import mate.academy.bookingservice.dto.payment.external.PaymentResultDto;
import mate.academy.bookingservice.dto.payment.internal.PaymentInfoDto;
import mate.academy.bookingservice.model.Payment;

public interface PaymentService {

    PaymentInfoDto getPaymentInfoDtoByUserId(Long userId);

    void handlePaymentCancellation(Long paymentId);

    Payment initPayment(Long bookingId, String userEmail);

    PaymentResultDto handleSuccess(String checkoutSessionId);
}
