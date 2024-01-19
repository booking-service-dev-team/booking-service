package mate.academy.bookingservice.dto.booking.internal;

import java.time.LocalDate;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class BookingDto {
    private Long id;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private Long accommodationId;
    private Long userId;
    private String statusName;
}
