package mate.academy.bookingservice.service.stripe;

import java.math.BigDecimal;
import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.model.checkout.Session;
import mate.academy.bookingservice.exception.CustomStripeException;

public interface StripeService {
    Customer createCustomer(String name, String email) throws StripeException;

    Session createStripePaymentSession(String productName,
                                       BigDecimal productPrice,
                                       String successUrl,
                                       String cancelUrl,
                                       String customerId) throws StripeException;
    boolean doesCustomerExist(String email) throws StripeException;

    Session getSessionByCheckoutSessionId(String checkoutSessionId) throws CustomStripeException;

    Customer getCustomerByEmail(String email);
}
