package mate.academy.bookingservice.service.user;

import lombok.RequiredArgsConstructor;
import mate.academy.bookingservice.dto.user.external.UserResponseDto;
import mate.academy.bookingservice.dto.user.internal.RoleRequestDto;
import mate.academy.bookingservice.dto.user.internal.UserRegistrationRequestDto;
import mate.academy.bookingservice.dto.user.internal.UserRequestDto;
import mate.academy.bookingservice.exception.EntityNotFoundException;
import mate.academy.bookingservice.exception.RegistrationException;
import mate.academy.bookingservice.mapper.UserMapper;
import mate.academy.bookingservice.model.User;
import mate.academy.bookingservice.repository.user.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    public static final String ADMIN_ROLE = "ROLE_ADMIN";
    public static final String CUSTOMER_ROLE = "ROLE_CUSTOMER";
    private final UserRepository userRepository;
    private final PasswordEncoder encoder;
    private final UserMapper userMapper;

    @Override
    public UserResponseDto registration(UserRegistrationRequestDto request)
            throws RegistrationException {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RegistrationException("Such email already exists");
        }
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(encoder.encode(request.getPassword()));
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setRole(User.Role.ROLE_CUSTOMER);
        User savedUser = userRepository.save(user);
        return userMapper.toUserResponseDto(savedUser);
    }

    @Override
    public UserResponseDto getUserInfo(Authentication authentication) {
        return userMapper.toUserResponseDto(getUserByAuthentication(authentication));
    }

    @Override
    public UserResponseDto updateUsersRole(Long userId, RoleRequestDto roleRequestDto) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new EntityNotFoundException("Can't find user by id " + userId)
        );
        if (roleRequestDto.getRole().equalsIgnoreCase(ADMIN_ROLE)) {
            user.setRole(User.Role.ROLE_ADMIN);
        } else if (roleRequestDto.getRole().equalsIgnoreCase(CUSTOMER_ROLE)) {
            user.setRole(User.Role.ROLE_CUSTOMER);
        }
        return userMapper.toUserResponseDto(userRepository.save(user));
    }

    @Override
    public UserResponseDto update(Authentication authentication, UserRequestDto requestDto) {
        User user = getUserByAuthentication(authentication);
        user.setEmail(requestDto.getEmail());
        user.setFirstName(requestDto.getFirstName());
        user.setLastName(requestDto.getLastName());
        return userMapper.toUserResponseDto(userRepository.save(user));
    }

    private User getUserByAuthentication(Authentication authentication) {
        return userRepository.findByEmail(authentication.getName()).orElseThrow(
                () -> new EntityNotFoundException("Can't find user by email: "
                        + authentication.getName())
        );
    }
}
