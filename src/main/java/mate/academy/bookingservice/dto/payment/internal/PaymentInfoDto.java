package mate.academy.bookingservice.dto.payment.internal;

import java.util.List;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class PaymentInfoDto {
    private List<PaymentDto> paymentDtos;
}
