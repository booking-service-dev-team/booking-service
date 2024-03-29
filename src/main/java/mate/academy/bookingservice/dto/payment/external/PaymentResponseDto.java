package mate.academy.bookingservice.dto.payment.external;

public record PaymentResponseDto(
        String message, String paymentId, String productName, String customerName
) {
}
