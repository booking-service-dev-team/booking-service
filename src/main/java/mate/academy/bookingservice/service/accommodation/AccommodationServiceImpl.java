package mate.academy.bookingservice.service.accommodation;

import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import mate.academy.bookingservice.dto.accommodation.external.AvailabilityRequestDto;
import mate.academy.bookingservice.dto.accommodation.external.CreateAccommodationRequestDto;
import mate.academy.bookingservice.dto.accommodation.external.PriceRequestDto;
import mate.academy.bookingservice.dto.accommodation.internal.AccommodationDto;
import mate.academy.bookingservice.dto.address.external.AddressRequestDto;
import mate.academy.bookingservice.exception.EntityNotFoundException;
import mate.academy.bookingservice.exception.IllegalAccommodationTypeArgument;
import mate.academy.bookingservice.mapper.AccommodationMapper;
import mate.academy.bookingservice.model.Accommodation;
import mate.academy.bookingservice.model.Address;
import mate.academy.bookingservice.repository.accommodation.AccommodationRepository;
import mate.academy.bookingservice.repository.accommodation.AddressRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class AccommodationServiceImpl implements AccommodationService {
    private final AccommodationRepository accommodationRepository;
    private final AddressRepository addressRepository;
    private final AccommodationMapper accommodationMapper;

    @SneakyThrows
    @Override
    public AccommodationDto save(CreateAccommodationRequestDto requestDto) {
        Address address = new Address()
                .setCountryName(requestDto.getCountryName())
                .setCityName(requestDto.getCityName())
                .setStreetName(requestDto.getStreetName())
                .setNumberOfHouse(requestDto.getNumberOfHouse());
        Address savedAddress = addressRepository.save(address);
        Accommodation accommodation = new Accommodation();
        accommodation.setAddress(savedAddress);
        try {
            accommodation.setType(Accommodation.Type.valueOf(requestDto.getTypeName()));
        } catch (Exception e) {
            throw new IllegalAccommodationTypeArgument("Incorrect value of accommodation type: "
                    + requestDto.getTypeName()
                    + ", use one of: " + Arrays.toString(Accommodation.Type.values()));
        }
        accommodation.setAmenities(requestDto.getAmenities());
        accommodation.setSizeOfAccommodation(String.valueOf(requestDto.getSizeOfAccommodation()));
        accommodation.setPricePerMonthUsd(requestDto.getPricePerMonthUsd());
        accommodation.setNumberOfAvailableAccommodation(requestDto
                .getNumberOfAvailableAccommodation());
        return accommodationMapper.toDto(accommodationRepository.save(accommodation));
    }

    @Override
    public String saveAll(CreateAccommodationRequestDto[] requestDtos) {
        Arrays.stream(requestDtos)
                .forEach(this::save);
        return "Accommodations saved successfully";
    }

    @Override
    public List<AccommodationDto> getAll(Pageable pageable) {
        return accommodationRepository.findAll(pageable).stream()
                .map(accommodationMapper::toDto)
                .toList();
    }

    @Override
    public AccommodationDto getById(Long id) {
        return accommodationMapper.toDto(accommodationRepository.findById(id)
                .orElseThrow(
                        () -> new EntityNotFoundException("Can't find accommodation by id: " + id)
                ));
    }

    @Override
    @Transactional
    public void updatePrice(PriceRequestDto requestDto, Long id) {
        Accommodation accommodation = accommodationRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(
                        "Can't find accommodation for update price by id: " + id)
        );
        accommodation.setPricePerMonthUsd(requestDto.getPricePerMonthUsd());
        accommodationRepository.save(accommodation);
    }

    @Override
    @Transactional
    public void updateAvailability(AvailabilityRequestDto requestDto, Long id) {
        Accommodation accommodation = accommodationRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(
                        "Can't find accommodation for update availability by id: " + id)
        );
        accommodation.setNumberOfAvailableAccommodation(requestDto
                .getNumberOfAvailableAccommodation());
        accommodationRepository.save(accommodation);
    }

    @Override
    @Transactional
    public void updateAddress(AddressRequestDto requestDto, Long id) {
        Accommodation accommodation = accommodationRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(
                        "Can't find accommodation for update address by id: " + id)
        );
        Address address = new Address()
                .setCountryName(requestDto.getCountryName())
                .setCityName(requestDto.getCityName())
                .setStreetName(requestDto.getStreetName())
                .setNumberOfHouse(requestDto.getNumberOfHouse());
        Address savedAddress = addressRepository.save(address);
        accommodation.setAddress(savedAddress);
        accommodationRepository.save(accommodation);
    }

    @Override
    public void deleteById(Long id) {
        accommodationRepository.deleteById(id);
    }
}
