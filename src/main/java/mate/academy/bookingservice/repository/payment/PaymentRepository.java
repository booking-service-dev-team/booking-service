package mate.academy.bookingservice.repository.payment;

import java.util.List;
import mate.academy.bookingservice.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> getPaymentsByBookingId(Long id);

    @Modifying
    @Query("UPDATE Payment SET status = :status WHERE id = :id")
    @Transactional
    void updatePaymentStatusById(Long id, Payment.Status status);

    @Query("SELECT p.bookingId FROM Payment p WHERE p.id = :id")
    Long getBookingIdByPaymentId(Long id);
}
