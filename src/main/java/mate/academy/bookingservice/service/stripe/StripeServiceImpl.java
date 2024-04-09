package mate.academy.bookingservice.service.stripe;

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
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import lombok.SneakyThrows;
import mate.academy.bookingservice.exception.CustomStripeException;
import mate.academy.bookingservice.exception.EntityNotFoundException;
import mate.academy.bookingservice.model.User;
import mate.academy.bookingservice.repository.user.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class StripeServiceImpl implements StripeService {
    private final UserRepository userRepository;

    public StripeServiceImpl(@Value("${stripe.api.key}") String stripeApiKey,
                             UserRepository userRepository) {
        this.userRepository = userRepository;
        Stripe.apiKey = stripeApiKey;
    }

    public Customer createCustomer(String name, String email) throws StripeException {
        CustomerCreateParams params =
                CustomerCreateParams.builder()
                        .setEmail(email)
                        .setName(name)
                        .build();

        return Customer.create(params);
    }

    public Session createStripePaymentSession(
            Long paymentId,
            String productName,
            BigDecimal productPrice,
            String successUrl,
            String cancelUrl,
            String customerId
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
                        .setSuccessUrl(successUrl)
                        .setCancelUrl(cancelUrl)
                        .addLineItem(
                                SessionCreateParams.LineItem.builder()
                                        .setPrice(price.getId())
                                        .setQuantity(1L)
                                        .build()
                        )
                        .putMetadata("productName", productName)
                        .putMetadata("paymentId", String.valueOf(paymentId))
                        .setMode(SessionCreateParams.Mode.PAYMENT)
                        .setCustomer(customerId)
                        .build();

        return Session.create(sessionParams);
    }

    @SneakyThrows
    public Session getSessionByCheckoutSessionId(String checkoutSessionId) {
        Session session;
        try {
            session = Session.retrieve(checkoutSessionId);
        } catch (StripeException e) {
            throw new CustomStripeException(
                    e.getMessage(),
                    e.getRequestId(),
                    e.getCode(),
                    e.getStatusCode()
            );
        }
        return session;
    }

    @SneakyThrows
    public Customer getCustomerByEmail(String email) {
        CustomerListParams params = CustomerListParams.builder()
                .setLimit(1L)
                .setEmail(email)
                .build();
        CustomerCollection customers;
        try {
            customers = Customer.list(params);
        } catch (StripeException e) {
            throw new CustomStripeException(
                    e.getMessage(),
                    e.getRequestId(),
                    e.getCode(),
                    e.getStatusCode()
            );
        }
        if (customers.getData().size() > 0) {
            return customers.getData().get(0);
        } else {
            User user = userRepository.findByEmail(email).orElseThrow(
                    () -> new EntityNotFoundException("Can't find user by email: " + email)
            );
            return createCustomer(user.getFirstName() + " " + user.getLastName(), email);
        }
    }

    @Override
    public Map<String, String> getPaymentDataByCheckoutSessionId(String checkoutSessionId) {
        Session session = getSessionByCheckoutSessionId(checkoutSessionId);
        Map<String, String> data = new HashMap<>();
        data.put("paymentId", session.getMetadata().get("paymentId"));
        data.put("productName", session.getMetadata().get("productName"));
        data.put("customerName", session.getCustomerDetails().getName());
        return data;
    }
}
