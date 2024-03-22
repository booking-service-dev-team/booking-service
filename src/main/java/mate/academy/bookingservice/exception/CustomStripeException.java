package mate.academy.bookingservice.exception;

import com.stripe.exception.StripeException;

public class CustomStripeException extends StripeException {
    public CustomStripeException(String message, String requestId, String code, Integer statusCode) {
        super(message, requestId, code, statusCode);
    }
}
