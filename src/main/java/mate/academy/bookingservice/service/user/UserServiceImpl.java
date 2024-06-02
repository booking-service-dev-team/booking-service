package mate.academy.bookingservice.service.user;

import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import mate.academy.bookingservice.dto.user.external.UserResponseDto;
import mate.academy.bookingservice.dto.user.internal.RoleRequestDto;
import mate.academy.bookingservice.dto.user.internal.UserRegistrationRequestDto;
import mate.academy.bookingservice.dto.user.internal.UserRequestDto;
import mate.academy.bookingservice.exception.EntityNotFoundException;
import mate.academy.bookingservice.exception.IllegalArgumentException;
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

    private final UserRepository userRepository;
    private final PasswordEncoder encoder;
    private final UserMapper userMapper;

    @SneakyThrows
    @Override
    public UserResponseDto registration(UserRegistrationRequestDto request) {
        if (emailExists(request.getEmail())) {
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

    @SneakyThrows
    @Override
    public UserResponseDto updateUsersRole(Long userId, RoleRequestDto roleRequestDto) {
        String role = roleRequestDto.getRole();
        User user = findUserById(userId);
        if (role.equalsIgnoreCase(User.Role.ROLE_ADMIN.toString())) {
            user.setRole(User.Role.ROLE_ADMIN);
        } else if (role.equalsIgnoreCase(User.Role.ROLE_CUSTOMER.toString())) {
            user.setRole(User.Role.ROLE_CUSTOMER);
        } else {
            throw new IllegalArgumentException("Incorrect value of user role: "
                    + " '" + role + "'"
                    + ", use one of: " + Arrays.toString(User.Role.values()));
        }
        return userMapper.toUserResponseDto(userRepository.save(user));
    }

    @SneakyThrows
    @Override
    public UserResponseDto update(Authentication authentication, UserRequestDto requestDto) {
        if (emailExists(requestDto.getEmail())) {
            throw new IllegalArgumentException("Such email already exists");
        }
        User user = getUserByAuthentication(authentication);
        user.setEmail(requestDto.getEmail());
        user.setFirstName(requestDto.getFirstName());
        user.setLastName(requestDto.getLastName());
        return userMapper.toUserResponseDto(userRepository.save(user));
    }

    @Override
    public User findUserById(Long id) {
        return userRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Can't find user by id: " + id)
        );
    }

    @Override
    public User getUserByAuthentication(Authentication authentication) {
        return userRepository.findByEmail(authentication.getName()).orElseThrow(
                () -> new EntityNotFoundException("Can't find user by email: "
                        + authentication.getName())
        );
    }

    private boolean emailExists(String email) {
        return userRepository.findByEmail(email).isPresent();
    }
}
