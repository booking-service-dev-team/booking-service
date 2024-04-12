package mate.academy.bookingservice.service.user;

import mate.academy.bookingservice.dto.user.external.UserResponseDto;
import mate.academy.bookingservice.dto.user.internal.RoleRequestDto;
import mate.academy.bookingservice.dto.user.internal.UserRegistrationRequestDto;
import mate.academy.bookingservice.dto.user.internal.UserRequestDto;
import mate.academy.bookingservice.exception.RegistrationException;
import mate.academy.bookingservice.model.User;
import org.springframework.security.core.Authentication;

public interface UserService {
    UserResponseDto registration(UserRegistrationRequestDto registrationRequestDto)
            throws RegistrationException;

    UserResponseDto getUserInfo(Authentication authentication);

    UserResponseDto updateUsersRole(Long usersId, RoleRequestDto roleRequestDto);

    UserResponseDto update(Authentication authentication, UserRequestDto requestDto);

    User findUserById(Long id);

    User getUserByAuthentication(Authentication authentication);
}
