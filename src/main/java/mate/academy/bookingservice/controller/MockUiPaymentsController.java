package mate.academy.bookingservice.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/ui/payments")
public class MockUiPaymentsController {

    @GetMapping("/success")
    public String handleSuccess() {
        return "success_page";
    }
}