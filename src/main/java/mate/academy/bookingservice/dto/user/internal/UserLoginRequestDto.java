package mate.academy.bookingservice.dto.user.internal;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public record UserLoginRequestDto(
        @NotEmpty
        @Size(min = 8, max = 50)
        @Email
        String email,
        @NotEmpty
        @Size(min = 4, max = 20)
        String password
) {
}
