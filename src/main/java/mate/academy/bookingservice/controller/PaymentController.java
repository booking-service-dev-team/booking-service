package mate.academy.bookingservice.controller;

import java.net.MalformedURLException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import com.stripe.exception.StripeException;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import mate.academy.bookingservice.dto.payment.external.CreatePaymentRequestDto;
import mate.academy.bookingservice.dto.payment.internal.PaymentInfoDto;
import mate.academy.bookingservice.model.Payment;
import mate.academy.bookingservice.service.payment.PaymentService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;
    @PostMapping()
    public ResponseEntity initPayment (
            Authentication authentication, @RequestBody CreatePaymentRequestDto requestDto
    ) throws MalformedURLException, StripeException {
        Payment payment = paymentService.initPayment(
                authentication.getName(),
                requestDto.getBookingId(),
                requestDto.getSuccessPageUrl(),
                requestDto.getCancelPageUrl());
        HttpHeaders headers = new HttpHeaders();
        headers.set("location", payment.getSessionUrl().toString());
        return ResponseEntity.status(HttpStatus.SEE_OTHER).headers(headers).build();
    }

    @GetMapping()
    @ResponseStatus(HttpStatus.OK)
    public PaymentInfoDto getPaymentInfoDtoByUserId(
            @RequestParam(name = "user_id", required = false) Long userId
    ) {
        return paymentService.getPaymentInfoDtoByUserId(userId);
    }

    @GetMapping("/success")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public ResponseEntity<Object> handleSuccess(
            @RequestParam(name = "payment_id") Long paymentId,
            @RequestParam(name = "ui_url") String uiUrl) {
        paymentService.handlePaymentSuccess(paymentId);
        HttpHeaders headers = new HttpHeaders();
        headers.set("location", URLDecoder.decode(uiUrl, StandardCharsets.UTF_8));
        return ResponseEntity.status(HttpStatus.SEE_OTHER).headers(headers).build();
    }

    @GetMapping("/cancel")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public String handleCancel(@RequestParam(name = "payment_id") Long paymentId) {
        paymentService.handlePaymentCancellation(paymentId);
        return "<html><body><h1>Ваше бронювання скасовано</h1></body></html>";
    }
}
