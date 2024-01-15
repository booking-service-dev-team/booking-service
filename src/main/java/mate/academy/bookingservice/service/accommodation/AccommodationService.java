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

    void updatePrice(PriceRequestDto requestDto, Long id);

    void updateAvailability(AvailabilityRequestDto requestDto, Long id);

    void updateAddress(AddressRequestDto requestDto, Long id);

    void deleteById(Long id);
}
