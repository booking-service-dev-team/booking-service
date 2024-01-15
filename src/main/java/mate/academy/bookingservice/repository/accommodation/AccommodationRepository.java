package mate.academy.bookingservice.repository.accommodation;

import java.util.Optional;
import mate.academy.bookingservice.model.Accommodation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccommodationRepository extends JpaRepository<Accommodation, Long> {
    @EntityGraph(attributePaths = "address")
    @Override
    Page<Accommodation> findAll(Pageable pageable);

    @EntityGraph(attributePaths = "address")
    @Override
    Optional<Accommodation> findById(Long id);
}
