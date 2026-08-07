package org.example.paymentservice.Controller;

import com.stripe.model.Event;
import com.stripe.model.Invoice;
import com.stripe.net.Webhook;
import org.example.paymentservice.Api.CompanyClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StripeWebhookController {

    @Value("${stripe.webhook.secret}")
    private String endpointSecret;

    @Value("${app.internal-key}")
    private String internalKey;

    private final CompanyClient companyClient;

    public StripeWebhookController(CompanyClient companyClient) {
        this.companyClient = companyClient;
    }

    @PostMapping("/webhook")
    public ResponseEntity<String> handleWebhook(
            @RequestBody String payload,
            @RequestHeader("Stripe-Signature") String sigHeader) {

        Event event;

        try {
            event = Webhook.constructEvent(payload, sigHeader, endpointSecret);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Invalid signature");
        }

        System.out.println("EVENT TYPE >>> " + event.getType());

        switch (event.getType()) {

            // 🔥 ПЛАЩАНЕ УСПЕШНО (initial checkout)
            case "checkout.session.completed" -> {

                try {

                    var json = com.google.gson.JsonParser.parseString(payload)
                            .getAsJsonObject();

                    var data = json.getAsJsonObject("data")
                            .getAsJsonObject("object");

                    String plan = data.getAsJsonObject("metadata")
                            .get("plan").getAsString();

                    String companyId = data.getAsJsonObject("metadata")
                            .get("companyId").getAsString();

                    String subscriptionId = data.get("subscription").getAsString();

                    System.out.println("PLAN >>> " + plan);
                    System.out.println("COMPANY >>> " + companyId);
                    System.out.println("SUB >>> " + subscriptionId);

                    companyClient.updatePlan(
                            Long.parseLong(companyId),
                            plan,
                            subscriptionId,
                            internalKey
                    );

                    System.out.println("PLAN UPDATED ✔");

                } catch (Exception e) {
                    System.out.println("ERROR PARSING SESSION");
                    e.printStackTrace();
                }
            }

            // 🔁 АВТО ПОДНОВЯВАНЕ
            case "invoice.payment_succeeded" -> {

                Invoice invoice = (Invoice) event.getDataObjectDeserializer()
                        .getObject()
                        .orElse(null);

                if (invoice == null) {
                    System.out.println("INVOICE NULL (skip)");
                    break;
                }

                String subscriptionId = invoice.getSubscription();

                companyClient.renewSubscription(subscriptionId, internalKey);

                System.out.println("RENEWED ✔ " + subscriptionId);
            }

            // ❌ НЕУСПЕШНО ПЛАЩАНЕ
            case "invoice.payment_failed" -> {

                Invoice invoice = (Invoice) event.getDataObjectDeserializer()
                        .getObject()
                        .orElse(null);

                if (invoice == null) {
                    System.out.println("INVOICE NULL (skip)");
                    break;
                }

                String subscriptionId = invoice.getSubscription();

                companyClient.deactivateSubscription(subscriptionId, internalKey);

                System.out.println("DEACTIVATED ❌ " + subscriptionId);
            }
        }

        return ResponseEntity.ok("OK");
    }
}
