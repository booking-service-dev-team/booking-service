package mate.academy.bookingservice.service.stripe;

import java.math.BigDecimal;
import java.net.URL;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;

public interface StripeService {
    void createCustomer(String name, String email) throws StripeException;

    Session createStripePaymentSession(String productName,
                                       BigDecimal productPrice,
                                       URL successUrl, URL cancelUrl, String customerEmail) throws StripeException;
    boolean doesCustomerExist(String email) throws StripeException;
}
