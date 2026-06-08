package org.example.paymentservice.Api;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "transport", url = "http://localhost:8080")
public interface CompanyClient {

    @PostMapping("/api/v1/company/update-plan")
    void updatePlan(@RequestParam Long companyId,
                    @RequestParam String plan,
                    @RequestParam String subscriptionId);

    @PostMapping("/api/v1/company/renew")
    void renewSubscription(@RequestParam String subscriptionId);

    @PostMapping("/api/v1/company/deactivate")
    void deactivateSubscription(@RequestParam String subscriptionId);

    @PostMapping("/api/v1/company/update-plan-by-subscription")
    void updatePlanBySubscription(@RequestParam String subscriptionId,
                                  @RequestParam String plan);
}
