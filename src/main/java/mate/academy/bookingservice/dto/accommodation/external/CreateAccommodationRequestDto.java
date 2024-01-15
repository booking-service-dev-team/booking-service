package mate.academy.bookingservice.dto.accommodation.external;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import lombok.Data;
import lombok.experimental.Accessors;
import mate.academy.bookingservice.model.Accommodation;
import mate.academy.bookingservice.validation.AccommodationType;

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
    private BigDecimal pricePerMonthUsd;
    @AccommodationType(enumClass = Accommodation.Type.class)
    private String typeName;
    private Integer numberOfAvailableAccommodation;
}
