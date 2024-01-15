package mate.academy.bookingservice.feature.uvecon.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class UCreateApartmentAccommodationRequestDto {
    @NotNull
    @JsonProperty("city")
    private String cityName;
    @NotNull
    @JsonProperty("street")
    private String streetName;
    @JsonProperty("house")
    private String numberOfHouse;
    @JsonProperty("size")
    private Integer sizeOfAccommodation;
    private String amenities;
    @NotNull
    @JsonProperty("price_per_month_usd")
    private BigDecimal pricePerMonthUsd;
}
