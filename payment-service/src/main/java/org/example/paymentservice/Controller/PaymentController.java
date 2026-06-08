package org.example.paymentservice.Controller;

import org.example.paymentservice.Service.PaymentService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/checkout")
    public String checkout(@RequestParam String plan,
                           @RequestParam Long companyId) throws Exception {

        plan = plan.toUpperCase();

        String priceId;

        if (plan.equals("PRO")) {
            priceId = "price_1TQpGBCvYGKkxV8X0ZlPcE6E";
        } else if (plan.equals("PREMIUM")) {
            priceId = "price_1TQpGaCvYGKkxV8XzNEYogm9";
        } else {
            throw new RuntimeException("Invalid plan: " + plan);
        }

        return paymentService.createCheckoutSession(priceId, plan, companyId);
    }
}
