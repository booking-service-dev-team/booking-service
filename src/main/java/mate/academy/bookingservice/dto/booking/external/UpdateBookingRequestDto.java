package mate.academy.bookingservice.dto.booking.external;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class UpdateBookingRequestDto {
    @NotNull
    private LocalDate checkInDate;
    @NotNull
    private LocalDate checkOutDate;
    @NotNull
    private Long accommodationId;
    @NotNull
    private Long userId;
    @NotNull
    private String statusName;
}
