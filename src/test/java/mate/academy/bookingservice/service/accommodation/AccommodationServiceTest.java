package mate.academy.bookingservice.service.accommodation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Optional;
import mate.academy.bookingservice.dto.accommodation.external.AvailabilityRequestDto;
import mate.academy.bookingservice.dto.accommodation.external.CreateAccommodationRequestDto;
import mate.academy.bookingservice.dto.accommodation.external.PriceRequestDto;
import mate.academy.bookingservice.dto.accommodation.internal.AccommodationDto;
import mate.academy.bookingservice.dto.address.external.AddressRequestDto;
import mate.academy.bookingservice.dto.address.internal.AddressDto;
import mate.academy.bookingservice.exception.IllegalArgumentException;
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
        accommodation.setAddress(savedAddress);
        accommodation.setType(Accommodation.Type.HOUSE);
        accommodation.setAmenities(requestDto.getAmenities());
        accommodation.setSizeOfAccommodation(requestDto.getSizeOfAccommodation());
        accommodation.setPricePerDayUsd(requestDto.getPricePerDayUsd());
        accommodation.setNumberOfAvailableAccommodation(requestDto
                .getNumberOfAvailableAccommodation());

        Accommodation savedAccommodation = new Accommodation();
        savedAccommodation.setId(1L);
        savedAccommodation.setAddress(savedAddress);
        savedAccommodation.setType(Accommodation.Type.HOUSE);
        savedAccommodation.setAmenities(requestDto.getAmenities());
        savedAccommodation.setSizeOfAccommodation(requestDto.getSizeOfAccommodation());
        savedAccommodation.setPricePerDayUsd(requestDto.getPricePerDayUsd());
        savedAccommodation.setNumberOfAvailableAccommodation(requestDto
                .getNumberOfAvailableAccommodation());

        AccommodationDto expected = new AccommodationDto()
                .setId(savedAccommodation.getId())
                .setType("HOUSE")
                .setAddress(new AddressDto()
                        .setId(savedAddress.getId())
                        .setCountryName(savedAddress.getCountryName())
                        .setCityName(savedAddress.getCityName())
                        .setStreetName(savedAddress.getStreetName())
                        .setNumberOfHouse(savedAddress.getNumberOfHouse()))
                .setSizeOfAccommodation(savedAccommodation.getSizeOfAccommodation())
                .setAmenities(savedAccommodation.getAmenities())
                .setPricePerDayUsd(savedAccommodation.getPricePerDayUsd())
                .setNumberOfAvailableAccommodation(savedAccommodation
                        .getNumberOfAvailableAccommodation());

        when(addressRepository.save(any(Address.class))).thenReturn(savedAddress);
        when(accommodationRepository.save(accommodation)).thenReturn(savedAccommodation);
        when(accommodationMapper.toDto(savedAccommodation)).thenReturn(expected);

        AccommodationDto actual = accommodationService.save(requestDto);

        assertNotNull(actual);
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Unsuccessful attempt to create a new accommodation with not available type")
    void save_WithNotValidAccommodationType_NotSuccess() {
        CreateAccommodationRequestDto requestDto = new CreateAccommodationRequestDto()
                .setCountryName("England")
                .setCityName("London")
                .setStreetName("Baker st.")
                .setNumberOfHouse("221B")
                .setSizeOfAccommodation("modern apartment")
                .setAmenities("fireplace")
                .setPricePerDayUsd(BigDecimal.TEN)
                .setTypeName("refrigerator box")
                .setNumberOfAvailableAccommodation(1);

        assertThrows(IllegalArgumentException.class, () -> {
            accommodationService.save(requestDto);
        });
    }

    @Test
    @DisplayName("Successfully created a few new accommodations")
    void saveAll_ValidRequestDto_Success() {
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
        accommodation.setAddress(savedAddress);
        accommodation.setType(Accommodation.Type.HOUSE);
        accommodation.setAmenities(requestDto.getAmenities());
        accommodation.setSizeOfAccommodation(requestDto.getSizeOfAccommodation());
        accommodation.setPricePerDayUsd(requestDto.getPricePerDayUsd());
        accommodation.setNumberOfAvailableAccommodation(requestDto
                .getNumberOfAvailableAccommodation());

        Accommodation savedAccommodation = new Accommodation();
        savedAccommodation.setId(1L);
        savedAccommodation.setAddress(savedAddress);
        savedAccommodation.setType(Accommodation.Type.HOUSE);
        savedAccommodation.setAmenities(requestDto.getAmenities());
        savedAccommodation.setSizeOfAccommodation(requestDto.getSizeOfAccommodation());
        savedAccommodation.setPricePerDayUsd(requestDto.getPricePerDayUsd());
        savedAccommodation.setNumberOfAvailableAccommodation(requestDto
                .getNumberOfAvailableAccommodation());

        AccommodationDto responseDto = new AccommodationDto()
                .setId(savedAccommodation.getId())
                .setType("HOUSE")
                .setAddress(new AddressDto()
                        .setId(savedAddress.getId())
                        .setCountryName(savedAddress.getCountryName())
                        .setCityName(savedAddress.getCityName())
                        .setStreetName(savedAddress.getStreetName())
                        .setNumberOfHouse(savedAddress.getNumberOfHouse()))
                .setSizeOfAccommodation(savedAccommodation.getSizeOfAccommodation())
                .setAmenities(savedAccommodation.getAmenities())
                .setPricePerDayUsd(savedAccommodation.getPricePerDayUsd())
                .setNumberOfAvailableAccommodation(savedAccommodation
                        .getNumberOfAvailableAccommodation());

        when(addressRepository.save(any(Address.class))).thenReturn(savedAddress);
        when(accommodationRepository.save(accommodation)).thenReturn(savedAccommodation);
        when(accommodationMapper.toDto(savedAccommodation)).thenReturn(responseDto);

        CreateAccommodationRequestDto[] requestDtos
                = new CreateAccommodationRequestDto[]{requestDto};

        String expected = "Accommodations saved successfully";
        String actual = accommodationService.saveAll(requestDtos);

        assertNotNull(actual);
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Successfully price updating by valid id")
    void updatePrice_ValidRequestDto_Success() {
        Long id = 1L;

        Accommodation accommodation = new Accommodation();
        accommodation.setId(id);
        accommodation.setPricePerDayUsd(BigDecimal.ZERO);

        PriceRequestDto requestDto = new PriceRequestDto()
                .setPricePerDayUsd(BigDecimal.valueOf(99));

        Accommodation updatedAccommodation = new Accommodation();
        updatedAccommodation.setId(id);
        updatedAccommodation.setPricePerDayUsd(requestDto.getPricePerDayUsd());

        AccommodationDto expected = new AccommodationDto()
                .setId(id)
                .setPricePerDayUsd(requestDto.getPricePerDayUsd());

        when(accommodationRepository.findById(id)).thenReturn(Optional.of(accommodation));
        when(accommodationRepository.save(updatedAccommodation))
                .thenReturn(updatedAccommodation);
        when(accommodationMapper.toDto(updatedAccommodation)).thenReturn(expected);

        AccommodationDto actual = accommodationService.updatePrice(requestDto, id);

        assertEquals(expected.getPricePerDayUsd(), actual.getPricePerDayUsd());
    }

    @Test
    @DisplayName("Successfully updating availability by valid id")
    void updateAvailability_ValidRequestDto_Success() {
        Long id = 1L;

        Accommodation accommodation = new Accommodation();
        accommodation.setId(id);
        accommodation.setNumberOfAvailableAccommodation(1);

        AvailabilityRequestDto requestDto = new AvailabilityRequestDto()
                .setNumberOfAvailableAccommodation(3);

        Accommodation updatedAccommodation = new Accommodation();
        updatedAccommodation.setId(id);
        updatedAccommodation
                .setNumberOfAvailableAccommodation(requestDto.getNumberOfAvailableAccommodation());

        AccommodationDto expected = new AccommodationDto()
                .setId(id)
                .setNumberOfAvailableAccommodation(requestDto.getNumberOfAvailableAccommodation());

        when(accommodationRepository.findById(id)).thenReturn(Optional.of(accommodation));
        when(accommodationRepository.save(updatedAccommodation))
                .thenReturn(updatedAccommodation);
        when(accommodationMapper.toDto(updatedAccommodation)).thenReturn(expected);

        AccommodationDto actual = accommodationService.updateAvailability(requestDto, id);

        assertEquals(expected.getNumberOfAvailableAccommodation(),
                actual.getNumberOfAvailableAccommodation());
    }

    @Test
    @DisplayName("Successfully updating address by valid id")
    void updateAddress_ValidRequestDto_Success() {

        Address address = new Address();
        address.setCountryName("England");
        address.setCityName("London");
        address.setStreetName("Baker st.");
        address.setNumberOfHouse("221B");

        Long id = 1L;

        Address updateAddress = new Address();
        updateAddress.setId(id);
        updateAddress.setCountryName("England");
        updateAddress.setCityName("London");
        updateAddress.setStreetName("Baker st.");
        updateAddress.setNumberOfHouse("221B");

        Accommodation accommodation = new Accommodation();
        accommodation.setAddress(address);

        AccommodationDto expected = new AccommodationDto()
                .setAddress(new AddressDto()
                        .setId(id)
                        .setCountryName("England")
                        .setCityName("London")
                        .setStreetName("Baker st.")
                        .setNumberOfHouse("221B"));

        when(accommodationRepository.findById(id)).thenReturn(Optional.of(accommodation));
        when(addressRepository.save(address)).thenReturn(updateAddress);
        when(accommodationRepository.save(accommodation)).thenReturn(accommodation);
        when(accommodationMapper.toDto(accommodation)).thenReturn(expected);

        AddressRequestDto requestDto = new AddressRequestDto()
                .setCountryName("England")
                .setCityName("London")
                .setStreetName("Baker st.")
                .setNumberOfHouse("221B");

        AccommodationDto actual = accommodationService.updateAddress(requestDto, id);

        assertEquals(expected, actual);
    }
}
