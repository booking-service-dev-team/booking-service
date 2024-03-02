package mate.academy.bookingservice.service.stripe;

import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.model.CustomerCollection;
import com.stripe.model.Price;
import com.stripe.model.Product;
import com.stripe.param.CustomerCreateParams;
import com.stripe.param.CustomerListParams;
import com.stripe.param.PriceCreateParams;
import com.stripe.param.ProductCreateParams;
import mate.academy.bookingservice.model.Booking;
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

    @Override
    public void createProduct(Booking booking) throws StripeException {
        ProductCreateParams params =
                ProductCreateParams.builder()
                        .setName(createProductNameFromBooking(booking))
                        .build();

        Product product = Product.create(params);

        PriceCreateParams priceParams =
                PriceCreateParams.builder()
                        .setCurrency("usd")
                        .setUnitAmount(calculatePriceInCentsUsdOfBooking(booking))
                        .setProduct(product.getId())
                        .build();

        Price price = Price.create(priceParams);

        Price priceFromStripe = Price.retrieve(price.getId());
        System.out.println(priceFromStripe.toJson());

        // todo create Stripe Session
//        SessionCreateParams sessionParams =
//                SessionCreateParams.builder()
//                        .setSuccessUrl("https://example.com/success")
//                        .addLineItem(
//                                SessionCreateParams.LineItem.builder()
//                                        .setPrice("price_1MotwRLkdIwHu7ixYcPLm5uZ")
//                                        .setQuantity(2L)
//                                        .build()
//                        )
//                        .setMode(SessionCreateParams.Mode.PAYMENT)
//                        .build();
//        Session session = Session.create(sessionParams);
//        Payment payment = new Payment()
//                .setStatus(Payment.Status.PAID)
//                .setBookingId(22L)
//                .setSessionUrl(new URL("http://test.com"))
//                .setSessionId("1234qwer")
//                .setAmountToPayUsd(BigDecimal.valueOf(100));
//        paymentRepository.save(payment);

    }

    public boolean doesCustomerExist(String email) throws StripeException {
        CustomerListParams params = CustomerListParams.builder().setLimit(1L).setEmail(email).build();
        CustomerCollection customers = Customer.list(params);
        return customers.getData().size() > 0;
    }

    private Long calculatePriceInCentsUsdOfBooking(Booking booking) {
        long numberOfRentedDays = ChronoUnit.DAYS.between(booking.getCheckInDate(), booking.getCheckOutDate());
        long pricePerDayInCentsUsd = booking.getAccommodation().getPricePerDayUsd()
                .multiply(BigDecimal.valueOf(100))
                .longValue();
        return numberOfRentedDays * pricePerDayInCentsUsd;
    }

    private String createProductNameFromBooking(Booking booking) {
        return booking.getAccommodation().getType()
                + " in "
                + booking.getAccommodation().getAddress().getCountryName()
                + ", "
                + booking.getAccommodation().getAddress().getCityName()
                + ", "
                + booking.getAccommodation().getAddress().getStreetName()
                + ", "
                + booking.getAccommodation().getAddress().getNumberOfHouse()
                + "; from: "
                + booking.getCheckInDate()
                + " to: "
                + booking.getCheckOutDate();

    }
}
