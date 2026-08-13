package org.example.transport_saas.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PaymentPageController {

    // Stripe пренасочва браузъра директно тук (може сесията вече да не е
    // валидна) - затова връщаме статична страница с ясно съобщение и линк
    // обратно в приложението, вместо redirect към защитен ресурс.
    @GetMapping("/success")
    public String success() {
        return "payment-success";
    }

    @GetMapping("/cancel")
    public String cancel() {
        return "payment-cancel";
    }
}
