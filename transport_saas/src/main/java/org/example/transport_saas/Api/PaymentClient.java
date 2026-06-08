package org.example.transport_saas.Api;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "payment", url = "http://localhost:8082")
public interface PaymentClient {

    @PostMapping("/api/v1/payments/checkout")
    String checkout(@RequestParam String plan,
                    @RequestParam Long companyId);


}
