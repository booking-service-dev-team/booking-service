package mate.academy.bookingservice.repository.payment;

import java.util.List;
import mate.academy.bookingservice.model.Payment;
import org.hibernate.annotations.processing.SQL;
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
}
