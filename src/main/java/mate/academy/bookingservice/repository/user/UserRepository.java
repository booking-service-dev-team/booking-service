package mate.academy.bookingservice.repository.user;

import java.util.Optional;
import mate.academy.bookingservice.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

//    @Query("SELECT User FROM Payment p WHERE p.id = :paymentId")
//    User getUserByPaymentId(Long paymentId);
}
