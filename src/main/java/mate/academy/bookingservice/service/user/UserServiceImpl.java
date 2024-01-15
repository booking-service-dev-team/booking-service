package mate.academy.bookingservice.service.user;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
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
    public static final String ADMIN_ROLE = "ADMIN";
    public static final String CUSTOMER_ROLE = "CUSTOMER";
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
        user.setRole(User.Role.CUSTOMER);
        User savedUser = userRepository.save(user);
        return userMapper.toUserResponseDto(savedUser);
    }

    @Override
    public UserResponseDto getUserInfo(Authentication authentication) {
        /*
         This is done in order to optimize database queries.
         */
        String regex = "User\\(id=(\\d+), email=(\\S+), password=.*?, firstName=(\\S+), "
                + "lastName=(\\S+), role=(\\S+), isDeleted=(\\S+)\\)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(authentication.getPrincipal().toString());
        if (matcher.matches()) {
            String id = matcher.group(1);
            String email = matcher.group(2);
            String firstName = matcher.group(3);
            String lastName = matcher.group(4);
            String role = matcher.group(5);
            return new UserResponseDto()
                    .setId(Long.valueOf(id))
                    .setEmail(email)
                    .setFirstName(firstName)
                    .setLastName(lastName)
                    .setRole(role);
        }
        return userMapper.toUserResponseDto(userRepository.findByEmail(authentication.getName())
                .orElseThrow(
                        () -> new EntityNotFoundException(
                                "You have not been found... no matter what.(."
                        )
                ));
    }

    @Override
    public UserResponseDto updateUsersRole(Long userId, RoleRequestDto roleRequestDto) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new EntityNotFoundException("Can't find user by id " + userId)
        );
        if (roleRequestDto.getRole().equalsIgnoreCase(ADMIN_ROLE)) {
            user.setRole(User.Role.ADMIN);
        } else if (roleRequestDto.getRole().equalsIgnoreCase(CUSTOMER_ROLE)) {
            user.setRole(User.Role.CUSTOMER);
        }
        return userMapper.toUserResponseDto(userRepository.save(user));
    }

    @Override
    public UserResponseDto update(Authentication authentication, UserRequestDto requestDto) {
        User user = userRepository.findByEmail(authentication.getName()).orElseThrow(
                () -> new EntityNotFoundException("Can't find user")
        );
        user.setEmail(requestDto.getEmail());
        user.setFirstName(requestDto.getFirstName());
        user.setLastName(requestDto.getLastName());
        return userMapper.toUserResponseDto(userRepository.save(user));
    }
}
