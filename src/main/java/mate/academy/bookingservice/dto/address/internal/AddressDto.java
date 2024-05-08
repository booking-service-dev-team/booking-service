package mate.academy.bookingservice.dto.address.internal;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class AddressDto {
    private Long id;
    private String countryName;
    private String cityName;
    private String streetName;
    private String numberOfHouse;
}
