package mate.academy.bookingservice.dto.address.external;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class AddressRequestDto {
    @NotBlank
    private String countryName;
    @NotBlank
    private String cityName;
    @NotBlank
    private String streetName;
    @NotBlank
    private String numberOfHouse;
}
