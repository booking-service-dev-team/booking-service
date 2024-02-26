package mate.academy.bookingservice.service.payment;

import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import com.stripe.exception.StripeException;
import com.stripe.model.Price;
import com.stripe.model.Product;
import com.stripe.model.checkout.Session;
import com.stripe.param.PriceCreateParams;
import com.stripe.param.ProductCreateParams;
import com.stripe.param.checkout.SessionCreateParams;
import lombok.RequiredArgsConstructor;
import mate.academy.bookingservice.dto.payment.internal.PaymentInfoDto;
import mate.academy.bookingservice.exception.EntityNotFoundException;
import mate.academy.bookingservice.mapper.PaymentMapper;
import mate.academy.bookingservice.model.Accommodation;
import mate.academy.bookingservice.model.Booking;
import mate.academy.bookingservice.model.Payment;
import mate.academy.bookingservice.model.User;
import mate.academy.bookingservice.repository.accommodation.AccommodationRepository;
import mate.academy.bookingservice.repository.booking.BookingRepository;
import mate.academy.bookingservice.repository.payment.PaymentRepository;
import mate.academy.bookingservice.repository.user.UserRepository;
import mate.academy.bookingservice.service.stripe.StripeService;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
    private final PaymentRepository paymentRepository;
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final PaymentMapper paymentMapper;
    private final StripeService stripeService;
    @Override
    public void initPaymentSession(String userEmail, Long bookingId)
            throws MalformedURLException, StripeException {
        if(!stripeService.doesCustomerExist(userEmail)) {
            User user = userRepository.findByEmail(userEmail).orElseThrow(
                    () -> new EntityNotFoundException("Can't find user by email: " + userEmail)
            );
            stripeService
                    .createCustomer(user.getFirstName() + " " + user.getLastName(), userEmail);
        }

        // todo create Stripe Session

        Booking booking = bookingRepository.findById(bookingId).orElseThrow(
                () -> new EntityNotFoundException("Can't find booking by id: " + bookingId)
        );

        ProductCreateParams params =
                ProductCreateParams.builder()
                        .setName(createProductNameFromBooking(booking))
                        .build();
        Product product = Product.create(params);
        System.out.println(product.toJson());

        PriceCreateParams priceParams =
                PriceCreateParams.builder()
                        .setCurrency("usd")
                        .setUnitAmount(calculatePriceInCentsUsdOfBooking(booking))
                        .setProduct(product.getId())
//                        .setRecurring(
//                                PriceCreateParams.Recurring.builder()
//                                        .setInterval(PriceCreateParams.Recurring.Interval.MONTH)
//                                        .build()
//                        )
//                        .setProductData(
//                                PriceCreateParams.ProductData.builder().setName("Gold Plan").build()
//                        )
                        .build();
        Price price = Price.create(priceParams);
        System.out.println(price.toJson());
        Price priceFromStripe = Price.retrieve(price.getId());
        System.out.println(priceFromStripe.toJson());

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

    @Override
    public PaymentInfoDto getPaymentInfoDtoByUserId(Long userId) {
        List<Booking> bookingsByUser = bookingRepository
                .getBookingsByUser(userRepository.findById(userId).orElseThrow(
                        () -> new EntityNotFoundException("Can't find user by id: " + userId)
                ));
        // todo znaity vsi payments z ysix bookings
        List<Payment> paymentsByBookingId = paymentRepository.getPaymentsByBookingId(bookingsByUser.get(0).getId());
        return new PaymentInfoDto().setPendingPayments(paymentsByBookingId.stream()
                .map(paymentMapper::toDto)
                .toList());
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
