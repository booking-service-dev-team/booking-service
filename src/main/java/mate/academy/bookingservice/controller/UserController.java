package mate.academy.bookingservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import mate.academy.bookingservice.dto.user.external.UserResponseDto;
import mate.academy.bookingservice.dto.user.internal.RoleRequestDto;
import mate.academy.bookingservice.dto.user.internal.UserRequestDto;
import mate.academy.bookingservice.service.user.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "User profile management",
        description = "Endpoints for user profile management")
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/me")
    @Operation(summary = "Getting user information ",
            description = "Retrieves the profile information for the currently logged-in user")
    public UserResponseDto getUserInfo(Authentication authentication) {
        return userService.getUserInfo(authentication);
    }

    @PutMapping("/{id}/role")
    @Operation(summary = "User role update", description = "Enables users to update their roles, "
            + "you can specify ADMIN or CUSTOMER, "
            + "role by default = CUSTOMER")
    public UserResponseDto updateUsersRole(@PathVariable Long id,
                                               @RequestBody RoleRequestDto roleRequestDto) {
        return userService.updateUsersRole(id, roleRequestDto);
    }

    @PutMapping("/me")
    @Operation(summary = "Updating user profile information",
            description = "Allows users to update their email, firstname and lastname")
    public UserResponseDto update(Authentication authentication,
                                  @RequestBody UserRequestDto requestDto) {
        return userService.update(authentication, requestDto);
    }
}
