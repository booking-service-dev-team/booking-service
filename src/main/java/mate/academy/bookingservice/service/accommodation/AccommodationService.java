package mate.academy.bookingservice.service.accommodation;

import java.util.List;
import mate.academy.bookingservice.dto.accommodation.external.AvailabilityRequestDto;
import mate.academy.bookingservice.dto.accommodation.external.CreateAccommodationRequestDto;
import mate.academy.bookingservice.dto.accommodation.external.PriceRequestDto;
import mate.academy.bookingservice.dto.accommodation.internal.AccommodationDto;
import mate.academy.bookingservice.dto.address.external.AddressRequestDto;
import org.springframework.data.domain.Pageable;

public interface AccommodationService {
    AccommodationDto save(CreateAccommodationRequestDto requestDto);

    String saveAll(CreateAccommodationRequestDto[] requestDtos);

    List<AccommodationDto> getAll(Pageable pageable);

    AccommodationDto getById(Long id);

    AccommodationDto updatePrice(PriceRequestDto requestDto, Long id);

    AccommodationDto updateAvailability(AvailabilityRequestDto requestDto, Long id);

    AccommodationDto updateAddress(AddressRequestDto requestDto, Long accommodationId);

    void deleteById(Long id);
}
