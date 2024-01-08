package mate.academy.bookingservice.service.user;

import lombok.RequiredArgsConstructor;
import mate.academy.bookingservice.dto.user.external.UserResponseDto;
import mate.academy.bookingservice.dto.user.internal.UserRegistrationRequestDto;
import mate.academy.bookingservice.exception.RegistrationException;
import mate.academy.bookingservice.mapper.UserMapper;
import mate.academy.bookingservice.model.User;
import mate.academy.bookingservice.repository.user.UserRepository;
//import mate.academy.bookingservice.service.shoppingcart.ShoppingCartService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder encoder;
    private final UserMapper userMapper;
//    private final ShoppingCartService shoppingCartService;

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
        User savedUser = userRepository.save(user);
//        shoppingCartService.registerNewShoppingCart(savedUser);
        return userMapper.toUserResponseDto(savedUser);
    }
}
