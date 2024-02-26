package mate.academy.bookingservice.dto.payment.external;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class CreatePaymentRequestDto {
    @NotNull
    private Long bookingId;
}
