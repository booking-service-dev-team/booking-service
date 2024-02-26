package mate.academy.bookingservice.service.stripe;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.model.CustomerCollection;
import com.stripe.param.CustomerCreateParams;
import com.stripe.param.CustomerListParams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class StripeServiceImpl implements StripeService {
    public StripeServiceImpl(@Value("${stripe.api.key}") String stripeApiKey) {
        Stripe.apiKey = stripeApiKey;
    }

    @Override
    public void createCustomer(String name, String email) throws StripeException {
        CustomerCreateParams params =
                CustomerCreateParams.builder()
                        .setEmail(email)
                        .setDescription("test Customer")
                        .setName(name)
                        .build();

        Customer customer = Customer.create(params);
        //todo delete this System.out.println at the end
        System.out.println("New customer create successfully:"
                + System.lineSeparator()
                + customer.toString());
    }

    public boolean doesCustomerExist(String email) throws StripeException {
        CustomerListParams params = CustomerListParams.builder().setLimit(1L).setEmail(email).build();
        CustomerCollection customers = Customer.list(params);
        return customers.getData().size() > 0;
    }
}
