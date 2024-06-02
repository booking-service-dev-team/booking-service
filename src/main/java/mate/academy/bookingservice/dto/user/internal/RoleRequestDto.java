package mate.academy.bookingservice.dto.user.internal;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class RoleRequestDto {
    @NotBlank
    private String role;
}
