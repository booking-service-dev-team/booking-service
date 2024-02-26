package mate.academy.bookingservice.repository.payment;

import java.util.List;
import mate.academy.bookingservice.model.Payment;
import org.hibernate.annotations.processing.SQL;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> getPaymentsByBookingId(Long id);

////    @Query("SELECT true limit 0")
//    @Query(nativeQuery = true, value = "SELECT true\n" +
//            "FROM\n" +
//            "    payments as p\n" +
//            "        inner join bookings as b\n" +
//            "                   on p.booking_id = b.id\n" +
//            "        inner join users u\n" +
//            "                   on b.user_id = u.id\n" +
//            "WHERE u.id = ?1\n" +
//            "LIMIT 1;")
//    boolean doesPaymentExistByUserId(Long userId);
}
