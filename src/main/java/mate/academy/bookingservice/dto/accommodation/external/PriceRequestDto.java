package mate.academy.bookingservice.dto.accommodation.external;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import lombok.Data;

@Data
public class PriceRequestDto {
    @NotNull
    @Min(0)
    private BigDecimal pricePerMonthUsd;
}
