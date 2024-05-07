package mate.academy.bookingservice.service.accommodation;

import mate.academy.bookingservice.mapper.AccommodationMapper;
import mate.academy.bookingservice.repository.accommodation.AccommodationRepository;
import mate.academy.bookingservice.repository.accommodation.AddressRepository;
import mate.academy.bookingservice.service.notification.NotificationService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AccommodationServiceTest {

    @Mock
    private AccommodationRepository accommodationRepository;

    @Mock
    private AddressRepository addressRepository;

    @Mock
    private AccommodationMapper accommodationMapper;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private AccommodationServiceImpl accommodationService;

    @Test
    void save() {
    }

    @Test
    void saveAll() {
    }

    @Test
    void getAll() {
    }

    @Test
    @DisplayName("Find by not valid id")
    void getById_WithNonAvailableAccommodationId_NotSuccess() {
    }

    @Test
    void updatePrice() {
    }

    @Test
    void updateAvailability() {
    }

    @Test
    void updateAddress() {
    }

    @Test
    void deleteById() {
    }
}
