package mate.academy.bookingservice.dto.payment.internal;

import java.math.BigDecimal;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class PaymentDto {
    private Long id;
    private String statusName;
    private Long bookingId;
    private String sessionUrl;
    private String sessionId;
    private BigDecimal amountToPayUsd;
}
