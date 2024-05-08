package mate.academy.bookingservice.service.accommodation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import mate.academy.bookingservice.dto.accommodation.external.CreateAccommodationRequestDto;
import mate.academy.bookingservice.dto.accommodation.internal.AccommodationDto;
import mate.academy.bookingservice.dto.address.internal.AddressDto;
import mate.academy.bookingservice.exception.EntityNotFoundException;
import mate.academy.bookingservice.mapper.AccommodationMapper;
import mate.academy.bookingservice.model.Accommodation;
import mate.academy.bookingservice.model.Address;
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

    // todo rewrite this shit
    @Test
    @DisplayName("Successfully created a new accommodation")
    void save_WithValidRequestDto_Success() {
        CreateAccommodationRequestDto requestDto = new CreateAccommodationRequestDto()
                .setCountryName("England")
                .setCityName("London")
                .setStreetName("Baker st.")
                .setNumberOfHouse("221B")
                .setSizeOfAccommodation("modern apartment")
                .setAmenities("fireplace")
                .setPricePerDayUsd(BigDecimal.TEN)
                .setTypeName("house")
                .setNumberOfAvailableAccommodation(1);

        Address savedAddress = new Address()
                .setId(1L)
                .setCountryName(requestDto.getCountryName())
                .setCityName(requestDto.getCityName())
                .setStreetName(requestDto.getStreetName())
                .setNumberOfHouse(requestDto.getNumberOfHouse());

        Accommodation accommodation = new Accommodation();
        accommodation.setId(1L);
        accommodation.setAddress(savedAddress);
        accommodation.setType(Accommodation.Type.HOUSE);
        accommodation.setAmenities(requestDto.getAmenities());
        accommodation.setSizeOfAccommodation(requestDto.getSizeOfAccommodation());
        accommodation.setPricePerDayUsd(requestDto.getPricePerDayUsd());
        accommodation.setNumberOfAvailableAccommodation(requestDto
                .getNumberOfAvailableAccommodation());

        AccommodationDto expected = new AccommodationDto()
                .setId(accommodation.getId())
                .setType("HOUSE")
                .setAddress(new AddressDto()
                        .setId(savedAddress.getId())
                        .setCountryName(savedAddress.getCountryName())
                        .setCityName(savedAddress.getCityName())
                        .setStreetName(savedAddress.getStreetName())
                        .setNumberOfHouse(savedAddress.getNumberOfHouse()))
                .setSizeOfAccommodation(accommodation.getSizeOfAccommodation())
                .setAmenities(accommodation.getAmenities())
                .setPricePerDayUsd(accommodation.getPricePerDayUsd())
                .setNumberOfAvailableAccommodation(accommodation.getNumberOfAvailableAccommodation());

        when(addressRepository.save(any(Address.class))).thenReturn(savedAddress);
        doReturn(accommodation).when(accommodationRepository).save(
                any(Accommodation.class)
        );
        when(accommodationMapper.toDto(accommodation)).thenReturn(expected);

        AccommodationDto actual = accommodationService.save(requestDto);

        assertNotNull(actual);
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Unsuccessful attempt to create a new accommodation with not available type")
    void save_WithNotValidAccommodationType_NotSuccess() {

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
        Long notValidId = 10L;

        when(accommodationRepository.findById(notValidId))
                .thenThrow(new EntityNotFoundException(
                        "Can't find accommodation by id: " + notValidId
                ));

        assertThrows(EntityNotFoundException.class, () -> {
           accommodationService.getById(notValidId);
        });
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
