package mate.academy.bookingservice.dto.accommodation.external;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class CreateAccommodationRequestDto {
    @NotNull
    private String countryName;
    @NotNull
    private String cityName;
    @NotNull
    private String streetName;
    private String numberOfHouse;
    private String sizeOfAccommodation;
    private String amenities;
    @NotNull
    @Min(0)
    private BigDecimal pricePerDayUsd;
    @NotNull
    private String typeName;
    @NotNull
    @Min(0)
    private Integer numberOfAvailableAccommodation;
}
