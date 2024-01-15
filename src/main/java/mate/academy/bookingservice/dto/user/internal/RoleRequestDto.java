package mate.academy.bookingservice.dto.user.internal;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RoleRequestDto {
    @NotBlank
    private String role;
}
