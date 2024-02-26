package mate.academy.bookingservice.controller;

//import com.stripe.model.Checkout;
import com.stripe.model.Event;
        import com.stripe.net.Webhook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

//@RestController
//@RequestMapping("/payments")
public class PaymentControllerByGpt {

    @Value("${stripe.api.key}")
    private String stripeApiKey; // Ваш секретний ключ Stripe

    @PostMapping("/create-customer")
    public String index() {

        return "TEST KEY:::" + stripeApiKey;
    }

//    @PostMapping("/")
//    public ResponseEntity<String> initiatePayment(@RequestParam("booking_id") Long bookingId) {
//        Stripe.apiKey = stripeApiKey;
//
//        // Логіка створення платіжного інтенту та отримання сесії оплати через Stripe Checkout
//        // Використайте відповідні дані для створення інтенту, такі як сума, валюта, тощо.
//
//        try {
//            // Створюємо платіжний інтент за допомогою Stripe API
//            PaymentIntent intent = PaymentIntent.create(/* ваши параметри */);
////
//            // Повертаємо ID платіжного інтенту для використання на клієнтському боці
//            return ResponseEntity.ok(intent.getId());
//        } catch (StripeException e) {
//            e.printStackTrace();
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Помилка ініціації платежу");
//        }
//    }

    @GetMapping("/")
    public ResponseEntity<String> getPaymentInfo(@RequestParam("user_id") Long userId) {
        // Логіка отримання інформації про платежі для користувача з вказаним ID
        // Використайте відповідні сервіси та репозиторії для обробки логіки.

        String paymentInfo = "Інформація про платежі для користувача з ID " + userId;
        return ResponseEntity.ok(paymentInfo);
    }

    @GetMapping("/success/")
    public ResponseEntity<String> handleSuccessfulPayment(@RequestParam("payment_intent_id") String paymentIntentId) {
        // Логіка обробки успішної оплати, наприклад, оновлення статусу бронювання та інше.

        String successMessage = "Платіж успішно оброблено. ID платіжного інтенту: " + paymentIntentId;
        return ResponseEntity.ok(successMessage);
    }

    @GetMapping("/cancel/")
    public ResponseEntity<String> handlePaymentCancellation() {
        // Логіка обробки скасування платежу, наприклад, повернення до сторінки бронювання.

        String cancellationMessage = "Платіж скасовано. Повернення до сторінки бронювання.";
        return ResponseEntity.ok(cancellationMessage);
    }

    // Webhook endpoint для обробки подій Stripe, наприклад, підтвердження оплати
    @PostMapping("/webhook")
    public ResponseEntity<String> handleStripeWebhook(@RequestBody String payload,
                                                      @RequestHeader("Stripe-Signature") String sigHeader) {
        // Логіка обробки подій Stripe через webhook

        try {
            Event event = Webhook.constructEvent(payload, sigHeader, stripeApiKey);
            // Обробка подій Stripe, наприклад, підтвердження оплати, оновлення статусу бронювання тощо.
            // Використовуйте event.getType() для визначення типу події.
            return ResponseEntity.ok("Webhook оброблено успішно");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Недійсний webhook");
        }
    }
}
