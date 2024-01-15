package mate.academy.bookingservice.dto.accommodation.external;

import java.math.BigDecimal;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
public class PriceRequestDto {
    @NotNull
    @Min(0)
    private BigDecimal pricePerMonthUsd;
}
