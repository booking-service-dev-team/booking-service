package mate.academy.bookingservice.service.stripe;

import com.stripe.exception.StripeException;

public interface StripeService {
    void createCustomer(String name, String email) throws StripeException;

    boolean doesCustomerExist(String email) throws StripeException;
}
