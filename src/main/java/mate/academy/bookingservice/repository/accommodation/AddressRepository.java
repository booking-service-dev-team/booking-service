package mate.academy.bookingservice.repository.accommodation;

import mate.academy.bookingservice.model.Address;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AddressRepository extends JpaRepository<Address, Long> {
}
