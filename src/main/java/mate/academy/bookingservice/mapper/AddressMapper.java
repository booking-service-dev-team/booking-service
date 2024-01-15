package mate.academy.bookingservice.mapper;

import mate.academy.bookingservice.config.MapperConfig;
import mate.academy.bookingservice.dto.address.internal.AddressDto;
import mate.academy.bookingservice.model.Address;
import org.mapstruct.Mapper;

@Mapper(config = MapperConfig.class)
public interface AddressMapper {
    AddressDto toDto(Address address);
}
