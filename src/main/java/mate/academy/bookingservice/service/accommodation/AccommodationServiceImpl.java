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
import mate.academy.bookingservice.exception.IllegalArgumentException;
import mate.academy.bookingservice.mapper.AccommodationMapper;
import mate.academy.bookingservice.model.Accommodation;
import mate.academy.bookingservice.model.Address;
import mate.academy.bookingservice.repository.accommodation.AccommodationRepository;
import mate.academy.bookingservice.repository.accommodation.AddressRepository;
import mate.academy.bookingservice.service.notification.NotificationService;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class AccommodationServiceImpl implements AccommodationService {
    private final AccommodationRepository accommodationRepository;
    private final AddressRepository addressRepository;
    private final AccommodationMapper accommodationMapper;
    private final NotificationService notificationService;

    // todo remove things that do not belong to the functionality of this class
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
        accommodation.setType(findAccommodationTypeValueByTypeName(requestDto.getTypeName()));
        accommodation.setAmenities(requestDto.getAmenities());
        accommodation.setSizeOfAccommodation(requestDto.getSizeOfAccommodation());
        accommodation.setPricePerDayUsd(requestDto.getPricePerDayUsd());
        accommodation.setNumberOfAvailableAccommodation(requestDto
                .getNumberOfAvailableAccommodation());
        Accommodation savedAccommodation = accommodationRepository.save(accommodation);
        sendMessage("Create new accommodation" + createMessageByAccommodation(savedAccommodation));
        return accommodationMapper.toDto(savedAccommodation);
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
        return accommodationMapper.toDto(findAccommodationById(id));
    }

    @Override
    @Transactional
    public void updatePrice(PriceRequestDto requestDto, Long id) {
        Accommodation accommodation = findAccommodationById(id);
        accommodation.setPricePerDayUsd(requestDto.getPricePerDayUsd());
        accommodationRepository.save(accommodation);
    }

    @Override
    @Transactional
    public void updateAvailability(AvailabilityRequestDto requestDto, Long id) {
        Accommodation accommodation = findAccommodationById(id);
        accommodation.setNumberOfAvailableAccommodation(requestDto
                .getNumberOfAvailableAccommodation());
        accommodationRepository.save(accommodation);
    }

    @Override
    @Transactional
    public void updateAddress(AddressRequestDto requestDto, Long id) {
        Accommodation accommodation = findAccommodationById(id);
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

    private Accommodation findAccommodationById(Long id) {
        return accommodationRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(
                        "Can't find accommodation by id: " + id)
        );
    }

    private Accommodation.Type findAccommodationTypeValueByTypeName(String typeName)
            throws IllegalArgumentException {
        if (Arrays.stream(Accommodation.Type.values())
                .map(String::valueOf)
                .noneMatch(s -> s.equals(typeName.toUpperCase()))) {
            throw new IllegalArgumentException("Incorrect value of accommodation type "
                    + typeName
                    + ", use one of:" + Arrays.toString(Accommodation.Type.values()));
        } else {
            return Accommodation.Type.valueOf(typeName.toUpperCase());
        }
    }

    private void sendMessage(String message) {
        notificationService.sendMessageToAdmins(message);
    }

    private String createMessageByAccommodation(Accommodation accommodation) {
        StringBuilder builder = new StringBuilder();
        builder.append(System.lineSeparator())
                .append("accommodation ID: ").append(accommodation.getId())
                .append(System.lineSeparator())
                .append("type: ").append(accommodation.getType())
                .append(System.lineSeparator())
                .append("size: ").append(accommodation.getSizeOfAccommodation())
                .append(System.lineSeparator())
                .append("city: ").append(accommodation.getAddress().getCityName())
                .append(System.lineSeparator())
                .append("street: ").append(accommodation.getAddress().getStreetName())
                .append(System.lineSeparator())
                .append("number of house: ").append(accommodation.getAddress().getNumberOfHouse());
        return builder.toString();
    }
}
