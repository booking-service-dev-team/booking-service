package mate.academy.bookingservice.service.stripe;

import com.stripe.exception.StripeException;
import mate.academy.bookingservice.model.Booking;

public interface StripeService {
    void createCustomer(String name, String email) throws StripeException;

    void createProduct(Booking booking) throws StripeException;

    boolean doesCustomerExist(String email) throws StripeException;
}
