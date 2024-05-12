package mate.academy.bookingservice.dto.accommodation.external;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class AvailabilityRequestDto {
    @NotNull
    @Min(0)
    private Integer numberOfAvailableAccommodation;
}
