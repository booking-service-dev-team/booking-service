package mate.academy.bookingservice.controller;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Queue;
import com.stripe.model.checkout.Session;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import mate.academy.bookingservice.dto.payment.external.CreatePaymentRequestDto;
import mate.academy.bookingservice.dto.payment.internal.PaymentInfoDto;
import mate.academy.bookingservice.model.Payment;
import mate.academy.bookingservice.service.payment.PaymentService;
import mate.academy.bookingservice.service.stripe.StripeService;
import org.springframework.beans.factory.annotation.Value;
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
    @Value("${payment-endpoint.success.mock-ui.url}") String successUrl;
    @Value("${payment-endpoint.cancel.mock-ui.url}") String cancelUrl;
    private final PaymentService paymentService;
    private final StripeService stripeService;

    @PostMapping()
    public ResponseEntity<Object> initPayment (
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
    public PaymentInfoDto getPaymentInfoDtoByUserId(
            @RequestParam(name = "user_id", required = false) Long userId
    ) {
        return paymentService.getPaymentInfoDtoByUserId(userId);
    }

    // todo add paymentId to metadata
    @SneakyThrows
    @GetMapping("/success")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public ResponseEntity<Object> handleSuccess(
            @RequestParam(name = "session_id") String checkoutSessionId
    ) {
        Session session = stripeService.getSessionByCheckoutSessionId(checkoutSessionId);
        String productName = session.getMetadata().get("productName");
        String customerName = session.getCustomerDetails().getName();
                HttpHeaders headers = new HttpHeaders();
        headers.set("location", URLDecoder.decode(successUrl
                        .concat("?" + "product_name" + "=" + productName)
                        .concat("&" + "customer_name" + "=" + customerName)
                , StandardCharsets.UTF_8));
        return ResponseEntity.status(HttpStatus.SEE_OTHER).headers(headers)
                .build();
    }

    @GetMapping("/cancel")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public String handleCancel(@RequestParam(name = "payment_id") Long paymentId) {
        paymentService.handlePaymentCancellation(paymentId);
        return "<html><body><h1>Ваше бронювання скасовано</h1></body></html>";
    }
}
