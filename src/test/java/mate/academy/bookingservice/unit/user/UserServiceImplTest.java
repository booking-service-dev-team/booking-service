package mate.academy.bookingservice.unit.user;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.util.Optional;
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
import mate.academy.bookingservice.service.user.UserServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder encoder;
    @Mock
    private UserMapper userMapper;
    @Mock
    private Authentication authentication;
    @InjectMocks
    private UserServiceImpl userService;

    @Test
    @DisplayName("Successful new user registration")
    void registration_ValidRequest_Success() {
        String testEmail = "jezza@examlpe.com";
        String encodedPassword = "encodedPassword";
        String firstName = "Jeremy";
        String lastName = "Clarkson";

        User user = new User();
        user.setEmail(testEmail);
        user.setPassword(encodedPassword);
        user.setRole(User.Role.ROLE_CUSTOMER);
        user.setFirstName(firstName);
        user.setLastName(lastName);

        Long userId = 1L;

        User savedUser = new User();
        savedUser.setId(userId);
        savedUser.setEmail(testEmail);
        savedUser.setPassword(encodedPassword);
        savedUser.setRole(User.Role.ROLE_CUSTOMER);
        savedUser.setFirstName(firstName);
        savedUser.setLastName(lastName);

        String testPassword = "password";

        UserResponseDto expected = new UserResponseDto()
                .setId(userId)
                .setEmail(testEmail)
                .setFirstName(firstName)
                .setLastName(lastName)
                .setRole(User.Role.ROLE_CUSTOMER.toString());

        when(userRepository.findByEmail(testEmail)).thenReturn(Optional.empty());
        when(encoder.encode(testPassword)).thenReturn(encodedPassword);
        when(userRepository.save(user)).thenReturn(savedUser);
        when(userMapper.toUserResponseDto(savedUser)).thenReturn(expected);

        UserResponseDto actual = userService.registration(new UserRegistrationRequestDto()
                .setEmail(testEmail)
                .setPassword(testPassword)
                .setFirstName(firstName)
                .setLastName(lastName)
        );

        assertNotNull(actual);
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Unsuccessful new user registration. With existing email")
    void registration_NotValidRequestWithExistingEmail_NotSuccess() {
        String testEmail = "jezza@examlpe.com";

        User user = new User();
        user.setEmail(testEmail);

        when(userRepository.findByEmail(testEmail)).thenReturn(Optional.of(user));

        assertThrows(RegistrationException.class, () -> {
            userService.registration(new UserRegistrationRequestDto()
                    .setEmail(testEmail));
        });
    }

    @Test
    @DisplayName("Successful getting information of logged-in user")
    void getUserInfo_ByAuthentication_Success() {
        String testEmail = "jezza@examlpe.com";
        String testPassword = "password";
        String firstName = "Jeremy";
        String lastName = "Clarkson";
        Long userId = 1L;

        User user = new User();
        user.setId(userId);
        user.setEmail(testEmail);
        user.setPassword(testPassword);
        user.setRole(User.Role.ROLE_CUSTOMER);
        user.setFirstName(firstName);
        user.setLastName(lastName);

        UserResponseDto expected = new UserResponseDto()
                .setId(userId)
                .setEmail(testEmail)
                .setFirstName(firstName)
                .setLastName(lastName)
                .setRole(User.Role.ROLE_CUSTOMER.toString());

        when(authentication.getName()).thenReturn(testEmail);
        when(userRepository.findByEmail(testEmail)).thenReturn(Optional.of(user));
        when(userMapper.toUserResponseDto(user)).thenReturn(expected);

        UserResponseDto actual = userService.getUserInfo(authentication);

        assertNotNull(actual);
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Unsuccessful getting user by not existing email")
    void getUserByAuthentication_WithNotValidEmail_NotSuccess() {
        String testEmail = "jezza@examlpe.com";

        when(authentication.getName()).thenReturn(testEmail);
        when(userRepository.findByEmail(testEmail)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            userService.getUserByAuthentication(authentication);
        });
    }

    @Test
    @DisplayName("Successful updating users role")
    void updateUsersRole_WithValidRequest_Success() {
        Long userId = 1L;

        User user = new User();
        user.setId(userId);

        UserResponseDto expected = new UserResponseDto()
                .setId(userId)
                .setRole(User.Role.ROLE_ADMIN.toString());

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toUserResponseDto(user)).thenReturn(expected);

        UserResponseDto actual = userService
                .updateUsersRole(userId, new RoleRequestDto().setRole("role_admin"));

        assertNotNull(actual);
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Successful users update")
    void update_WithValidRequest_Success() {
        UserRequestDto requestDto = new UserRequestDto()
                .setEmail("jezza@example.uk")
                .setFirstName("Jeremy")
                .setLastName("Clarkson");

        User user = new User();
        user.setId(3L);
        user.setEmail("hammond@example.com");
        user.setFirstName("Richard");
        user.setLastName("Hammond");

        UserResponseDto expected = new UserResponseDto()
                .setEmail(requestDto.getEmail())
                .setFirstName(requestDto.getFirstName())
                .setLastName(requestDto.getLastName());

        when(userRepository.findByEmail(requestDto.getEmail())).thenReturn(Optional.empty());
        when(authentication.getName()).thenReturn(user.getEmail());
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toUserResponseDto(user)).thenReturn(expected);

        UserResponseDto actual = userService.update(authentication, requestDto);

        assertNotNull(actual);
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Unsuccessful attempt updating users role by not valid request")
    void updateUsersRole_WithNotValidRequest_NotSuccess() {
        Long userId = 1L;
        String nothingRole = "role_nothing";

        User user = new User();
        user.setId(userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        assertThrows(IllegalArgumentException.class, () -> {
            userService.updateUsersRole(userId, new RoleRequestDto()
                    .setRole(nothingRole));
        });
    }

    @Test
    @DisplayName("Unsuccessful attempt updating user by not valid request with existing email")
    void update_WithExistingEmail_NotSuccess() {

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(new User()));

        assertThrows(IllegalArgumentException.class, () -> {
            userService.update(authentication,
                    new UserRequestDto().setEmail("existing-email@example.com"));
        });
    }

    @Test
    @DisplayName("Unsuccessful attempt getting user by not existing id")
    void findUserById_WithNotExistingId_NotSuccess() {

        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            userService.findUserById(anyLong());
        });
    }
}
