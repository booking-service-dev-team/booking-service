package mate.academy.bookingservice.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MockUiController {

    @GetMapping("/success")
    public String handleSuccess() {
        return "success_page"; // повертає ім'я файлу без розширення
    }
}