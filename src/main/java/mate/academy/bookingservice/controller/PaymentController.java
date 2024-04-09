package mate.academy.bookingservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import mate.academy.bookingservice.dto.payment.external.CreatePaymentRequestDto;
import mate.academy.bookingservice.dto.payment.external.PaymentResponseDto;
import mate.academy.bookingservice.dto.payment.internal.PaymentInfoDto;
import mate.academy.bookingservice.model.Payment;
import mate.academy.bookingservice.service.payment.PaymentService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
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
    @Operation(summary = "Initialize payment",
            description = "Starts the payment process for a specific booking. "
                    + "First, it will check whether a user with such an email is registered "
                    + "in the payment system, and if not, it will create one. "
                    + "After additional checks of the booking, the payment is initiated "
                    + "directly in the payment system.")
    public ResponseEntity<Object> initPayment(
            Authentication authentication, @RequestBody CreatePaymentRequestDto requestDto
    ) {
        Payment payment = paymentService.initPayment(requestDto.getBookingId(),
                authentication.getName());
        HttpHeaders headers = new HttpHeaders();
        headers.set("location", payment.getSessionUrl().toString());
        return ResponseEntity.status(HttpStatus.SEE_OTHER).headers(headers).build();
    }

    @GetMapping()
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Getting user's payment information by user ID",
            description = "Getting information about user's payments by user ID. Only for admins.")
    public PaymentInfoDto getPaymentInfoDtoByUserId(
            @RequestParam(name = "user_id") Long userId
    ) {
        return paymentService.getPaymentInfoDtoByUserId(userId);
    }

    @GetMapping("/my")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Getting payment information of logged-in user.",
            description = "Getting payment information of logged-in user.")
    public PaymentInfoDto getPaymentInfoDtoByLoggedInUser(Authentication authentication) {
        return paymentService.getPaymentInfoDtoByLoggedInUser(authentication);
    }

    @GetMapping("/success")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "In case of success payment.",
            description = "Getting information about success payment.")
    @ResponseBody
    public PaymentResponseDto handleSuccess(
            @RequestParam(name = "session_id") String checkoutSessionId
    ) {
        return paymentService.handleSuccess(checkoutSessionId);
    }

    @GetMapping("/cancel")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "In case of cancellation of payment.",
            description = "Getting information about canceled payment.")
    @ResponseBody
    public PaymentResponseDto handleCancel(
            @RequestParam(name = "session_id") String checkoutSessionId
    ) {
        return paymentService.handleCancel(checkoutSessionId);
    }
}
