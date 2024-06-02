package mate.academy.bookingservice.dto.user.internal;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.experimental.Accessors;
import mate.academy.bookingservice.validation.FieldMatch;

@Data
@Accessors(chain = true)
@FieldMatch.List({
        @FieldMatch(first = "password", second = "repeatPassword")
})
public class UserRegistrationRequestDto {
    @NotBlank
    @Size(min = 8, max = 50)
    @Email
    private String email;

    @NotBlank
    @Size(min = 4, max = 20)
    private String password;

    @NotBlank
    @Size(min = 4, max = 20)
    private String repeatPassword;

    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;
}
