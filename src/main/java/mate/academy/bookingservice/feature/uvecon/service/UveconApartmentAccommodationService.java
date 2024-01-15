package mate.academy.bookingservice.feature.uvecon.service;

import mate.academy.bookingservice.dto.accommodation.internal.AccommodationDto;
import mate.academy.bookingservice.feature.uvecon.dto.UCreateApartmentAccommodationRequestDto;

public interface UveconApartmentAccommodationService {
    AccommodationDto saveApartmentUkraine(UCreateApartmentAccommodationRequestDto requestDto);

    String saveAllApartmentUkraine(UCreateApartmentAccommodationRequestDto[] requestDtos);
}
