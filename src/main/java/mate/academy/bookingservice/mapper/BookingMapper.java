package mate.academy.bookingservice.mapper;

import mate.academy.bookingservice.config.MapperConfig;
import mate.academy.bookingservice.dto.booking.internal.BookingDto;
import mate.academy.bookingservice.model.Booking;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapperConfig.class, uses = AccommodationMapper.class)
public interface BookingMapper {

    @Mapping(source = "accommodation.id", target = "accommodationId")
    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "status", target = "statusName")
    BookingDto toDto(Booking booking);
}
