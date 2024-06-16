package mate.academy.bookingservice.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;
import mate.academy.bookingservice.AbstractIntegrationTest;
import mate.academy.bookingservice.model.Payment;
import mate.academy.bookingservice.repository.payment.PaymentRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Sql(scripts = {
        "classpath:databases/users/add-user-to-users-table.sql",
        "classpath:databases/addresses/add-addresses-to-addresses-table.sql",
        "classpath:databases/accommodations/add-accommodations-to-accommodations-table.sql",
        "classpath:databases/bookings/add-booking-to-bookings-table.sql",
        "classpath:databases/payments/add-payment-to-payments-table.sql"
}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
@Sql(scripts = {
        "classpath:databases/payments/remove-all-from-payments-table.sql",
        "classpath:databases/bookings/remove-all-from-bookings-table.sql",
        "classpath:databases/accommodations/remove-all-from-accommodations-table.sql",
        "classpath:databases/addresses/remove-all-from-addresses-table.sql",
        "classpath:databases/users/remove-all-from-users-table.sql"
}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_CLASS)
public class PaymentRepositoryTest extends AbstractIntegrationTest {

    @Autowired
    private PaymentRepository paymentRepository;

    @Test
    @DisplayName("Successfully update payment status by id and Payment Status")
    void updatePaymentStatusById_ShouldUpdatePaymentStatusById() {
        paymentRepository.updatePaymentStatusById(100L, Payment.Status.PAID);

        Optional<Payment> actual = paymentRepository.findById(100L);

        assertTrue(actual.isPresent());
        assertEquals("PAID", actual.get().getStatus().name());
    }

    @Test
    @DisplayName("Getting booking id by payment id")
    void getBookingIdByPaymentId_ShouldReturnBookingIdByPaymentId() {
        Long actual = paymentRepository.getBookingIdByPaymentId(100L);

        assertEquals(1L, actual);
    }
}
