package mate.academy.bookingservice.service.user;

import mate.academy.bookingservice.dto.user.external.UserResponseDto;
import mate.academy.bookingservice.dto.user.internal.UserRegistrationRequestDto;
import mate.academy.bookingservice.exception.RegistrationException;

public interface UserService {
    UserResponseDto registration(UserRegistrationRequestDto registrationRequestDto)
            throws RegistrationException;
}
