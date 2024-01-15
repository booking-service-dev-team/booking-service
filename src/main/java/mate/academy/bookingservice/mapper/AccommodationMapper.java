package mate.academy.bookingservice.mapper;

import mate.academy.bookingservice.config.MapperConfig;
import mate.academy.bookingservice.dto.accommodation.internal.AccommodationDto;
import mate.academy.bookingservice.model.Accommodation;
import org.mapstruct.Mapper;

@Mapper(config = MapperConfig.class, uses = AddressMapper.class)
public interface AccommodationMapper {
    AccommodationDto toDto(Accommodation accommodation);

    Accommodation toModel(AccommodationDto accommodationDto);
}
