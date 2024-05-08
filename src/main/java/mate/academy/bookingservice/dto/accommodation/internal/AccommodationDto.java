package mate.academy.bookingservice.dto.accommodation.internal;

import java.math.BigDecimal;
import lombok.Data;
import lombok.experimental.Accessors;
import mate.academy.bookingservice.dto.address.internal.AddressDto;

@Data
@Accessors(chain = true)
public class AccommodationDto {
    private Long id;
    private String type;
    private AddressDto address;
    private String sizeOfAccommodation;
    private String amenities;
    private BigDecimal pricePerDayUsd;
    private Integer numberOfAvailableAccommodation;
}
