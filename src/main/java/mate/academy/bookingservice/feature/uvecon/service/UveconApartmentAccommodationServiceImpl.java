package mate.academy.bookingservice.feature.uvecon.service;

import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import mate.academy.bookingservice.dto.accommodation.internal.AccommodationDto;
import mate.academy.bookingservice.feature.uvecon.dto.UCreateApartmentAccommodationRequestDto;
import mate.academy.bookingservice.mapper.AccommodationMapper;
import mate.academy.bookingservice.model.Accommodation;
import mate.academy.bookingservice.model.Address;
import mate.academy.bookingservice.repository.accommodation.AccommodationRepository;
import mate.academy.bookingservice.repository.accommodation.AddressRepository;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UveconApartmentAccommodationServiceImpl implements
        UveconApartmentAccommodationService {
    private final AccommodationRepository accommodationRepository;
    private final AddressRepository addressRepository;
    private final AccommodationMapper accommodationMapper;

    @Override
    public AccommodationDto saveApartmentUkraine(
            UCreateApartmentAccommodationRequestDto requestDto
    ) {
        Address address = new Address()
                .setCountryName("Ukraine")
                .setCityName(requestDto.getCityName())
                .setStreetName(requestDto.getStreetName())
                .setNumberOfHouse(requestDto.getNumberOfHouse());
        Address savedAddress = addressRepository.save(address);
        Accommodation accommodation = new Accommodation()
                .setAddress(savedAddress)
                .setType(Accommodation.Type.APARTMENT)
                .setAmenities(requestDto.getAmenities())
                .setSizeOfAccommodation(String.valueOf(requestDto.getSizeOfAccommodation())
                        .concat(" кімн."))
                .setPricePerMonthUsd(requestDto.getPricePerMonthUsd())
                .setNumberOfAvailableAccommodation(1);
        return accommodationMapper.toDto(accommodationRepository.save(accommodation));
    }

    @Override
    public String saveAllApartmentUkraine(UCreateApartmentAccommodationRequestDto[] requestDtos) {
        Arrays.stream(requestDtos)
                .forEach(this::saveApartmentUkraine);
        return "Apartment accommodations saved successfully";
    }
}
