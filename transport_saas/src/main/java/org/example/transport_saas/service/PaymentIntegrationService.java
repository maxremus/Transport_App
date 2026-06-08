package org.example.transport_saas.service;

import org.example.transport_saas.Api.PaymentClient;
import org.example.transport_saas.auth.SecurityUtils;
import org.springframework.stereotype.Service;

@Service
public class PaymentIntegrationService {

    private final PaymentClient paymentClient;

    public PaymentIntegrationService(PaymentClient paymentClient) {
        this.paymentClient = paymentClient;
    }

    public String createCheckout(String plan) {

        Long companyId = SecurityUtils.getCurrentCompanyId();

        return paymentClient.checkout(plan, companyId);
    }
}
