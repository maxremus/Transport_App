package org.example.transport_saas.Api;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "payment", url = "${payment.service.url}")
public interface PaymentClient {

    @PostMapping("/api/v1/payments/checkout")
    String checkout(@RequestParam String plan,
                    @RequestParam Long companyId);


}
