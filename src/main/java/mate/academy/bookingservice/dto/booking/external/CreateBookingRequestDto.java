package mate.academy.bookingservice.dto.booking.external;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

@Data
@Accessors(chain = true)
public class CreateBookingRequestDto {
    @NotNull
    @DateTimeFormat
    private LocalDate checkInDate;
    @NotNull
    @DateTimeFormat
    private LocalDate checkOutDate;
    @NotNull
    private Long accommodationId;
}
