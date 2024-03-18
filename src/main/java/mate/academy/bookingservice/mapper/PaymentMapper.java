package mate.academy.bookingservice.mapper;

import mate.academy.bookingservice.config.MapperConfig;
import mate.academy.bookingservice.dto.payment.internal.PaymentDto;
import mate.academy.bookingservice.model.Payment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapperConfig.class)
public interface PaymentMapper {
    Payment toModel(PaymentDto paymentDto);

    @Mapping(source = "status", target = "statusName")
    PaymentDto toDto(Payment payment);
}
