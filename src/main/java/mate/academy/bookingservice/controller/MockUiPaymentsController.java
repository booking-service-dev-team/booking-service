package mate.academy.bookingservice.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/mock-ui/payments")
public class MockUiPaymentsController {

    @GetMapping("/success")
    public String handleSuccess(
            @RequestParam(name = "product_name") String productName,
            @RequestParam(name = "customer_name") String customerName,
            Model model
    ) {
        model.addAttribute("productName", productName);
        model.addAttribute("customerName", customerName);
        return "success_page";
    }
}
