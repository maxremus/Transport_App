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

        System.out.println("📩 /webhook получи заявка от Stripe");

        Event event;

        try {
            event = Webhook.constructEvent(payload, sigHeader, endpointSecret);
        } catch (Exception e) {
            System.out.println("❌ INVALID STRIPE SIGNATURE - проверете дали STRIPE_WEBHOOK_SECRET " +
                    "съвпада с 'Signing secret' на webhook endpoint-а в Stripe Dashboard");
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Invalid signature");
        }

        System.out.println("EVENT TYPE >>> " + event.getType());

        // ВАЖНО: ако тук нещо гръмне (напр. transport_saas временно спи на
        // Render Free plan), трябва да върнем НЕуспешен статус, за да може
        // Stripe да пробва отново автоматично (Stripe retry-ва неуспешни
        // webhook-и с нарастващи интервали до 3 дни). Ако винаги връщаме
        // 200 OK, Stripe мисли, че всичко е наред и никога не пробва пак -
        // тогава планът никога не се обновява.
        boolean success = true;

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
                    System.out.println("ERROR PARSING/UPDATING PLAN - ще накараме Stripe да пробва пак");
                    e.printStackTrace();
                    success = false;
                }
            }

            // 🔁 АВТО ПОДНОВЯВАНЕ
            case "invoice.payment_succeeded" -> {

                try {
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

                } catch (Exception e) {
                    System.out.println("ERROR RENEWING SUBSCRIPTION - ще накараме Stripe да пробва пак");
                    e.printStackTrace();
                    success = false;
                }
            }

            // ❌ НЕУСПЕШНО ПЛАЩАНЕ
            case "invoice.payment_failed" -> {

                try {
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

                } catch (Exception e) {
                    System.out.println("ERROR DEACTIVATING SUBSCRIPTION - ще накараме Stripe да пробва пак");
                    e.printStackTrace();
                    success = false;
                }
            }
        }

        if (!success) {
            // 500 -> Stripe ще счита доставката за неуспешна и ще пробва пак
            // автоматично (обикновено първи retry след няколко минути,
            // достатъчно време transport_saas да се е събудил).
            return ResponseEntity.status(500).body("Internal processing failed - please retry");
        }

        return ResponseEntity.ok("OK");
    }
}
