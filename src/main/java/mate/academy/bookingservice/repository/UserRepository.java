package mate.academy.bookingservice.repository;

import mate.academy.bookingservice.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
