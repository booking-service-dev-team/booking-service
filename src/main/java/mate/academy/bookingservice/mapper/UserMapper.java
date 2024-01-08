package mate.academy.bookingservice.mapper;

import mate.academy.bookingservice.config.MapperConfig;
import mate.academy.bookingservice.dto.user.external.UserResponseDto;
import mate.academy.bookingservice.model.User;
import org.mapstruct.Mapper;

@Mapper(config = MapperConfig.class)
public interface UserMapper {
    UserResponseDto toUserResponseDto(User user);
}
