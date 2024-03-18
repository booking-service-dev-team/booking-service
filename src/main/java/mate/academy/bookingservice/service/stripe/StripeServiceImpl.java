package mate.academy.bookingservice.service.stripe;

import java.math.BigDecimal;
import java.net.URL;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.model.CustomerCollection;
import com.stripe.model.Price;
import com.stripe.model.Product;
import com.stripe.model.checkout.Session;
import com.stripe.param.CustomerCreateParams;
import com.stripe.param.CustomerListParams;
import com.stripe.param.PriceCreateParams;
import com.stripe.param.ProductCreateParams;
import com.stripe.param.checkout.SessionCreateParams;
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
    }

    @Override
    public Session createStripePaymentSession(
            String productName,
            BigDecimal productPrice,
            URL successUrl,
            URL cancelUrl,
            String customerEmail
    ) throws StripeException {
        ProductCreateParams params =
                ProductCreateParams.builder()
                        .setName(productName)
                        .build();

        Product product = Product.create(params);

        PriceCreateParams priceParams =
                PriceCreateParams.builder()
                        .setCurrency("usd")
                        .setUnitAmount(productPrice.longValue() * 100)
                        .setProduct(product.getId())
                        .build();

        Price price = Price.create(priceParams);

        SessionCreateParams sessionParams =
                SessionCreateParams.builder()
                        .setSuccessUrl(successUrl.toString())
                        .setCancelUrl(cancelUrl.toString())
                        .addLineItem(
                                SessionCreateParams.LineItem.builder()
                                        .setPrice(price.getId())
                                        .setQuantity(1L)
                                        .build()
                        )
                        .setMode(SessionCreateParams.Mode.PAYMENT)
                        .setCustomerEmail(customerEmail)
                        .build();

        return Session.create(sessionParams);
    }

    public boolean doesCustomerExist(String email) throws StripeException {
        CustomerListParams params = CustomerListParams.builder().setLimit(1L).setEmail(email).build();
        CustomerCollection customers = Customer.list(params);
        return customers.getData().size() > 0;
    }
}
