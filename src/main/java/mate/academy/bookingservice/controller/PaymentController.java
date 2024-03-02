package mate.academy.bookingservice.controller;

import java.net.MalformedURLException;
import com.stripe.exception.StripeException;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import mate.academy.bookingservice.dto.payment.external.CreatePaymentRequestDto;
import mate.academy.bookingservice.dto.payment.internal.PaymentInfoDto;
import mate.academy.bookingservice.model.User;
import mate.academy.bookingservice.service.payment.PaymentService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Payment management",
        description = "Endpoints for managing payments.")
@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;
    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public void initPaymentSession (
            Authentication authentication, @RequestBody CreatePaymentRequestDto requestDto
    ) throws MalformedURLException, StripeException {
//        User user = (User) authentication.getPrincipal();
        paymentService.initPaymentSession(authentication.getName(), requestDto.getBookingId());
//        return new InitPaymentSessionResultDto();
    }

    @GetMapping()
    @ResponseStatus(HttpStatus.OK)
    public PaymentInfoDto getPaymentInfoDtoByUserId(@RequestParam(name = "user_id", required = false) Long userId) {
        return paymentService.getPaymentInfoDtoByUserId(userId);
    }
}
