package org.example.paymentservice.Service;


import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import org.springframework.stereotype.Service;
import com.stripe.Stripe;
import org.springframework.beans.factory.annotation.Value;


@Service
public class PaymentService {

    @Value("${stripe.secret.key}")
    private String stripeSecretKey;

    // URL-ът на transport_saas (публично достъпен), защото там живеят
    // страниците /success и /cancel, към които Stripe пренасочва браузъра
    @Value("${app.frontend-url}")
    private String baseUrl;

    public String createCheckoutSession(String priceId, String plan, Long companyId) throws Exception {

        Stripe.apiKey = stripeSecretKey;

        SessionCreateParams params =
                SessionCreateParams.builder()
                        .setMode(SessionCreateParams.Mode.SUBSCRIPTION)

                        .setSuccessUrl(baseUrl + "/success?success=true")
                        .setCancelUrl(baseUrl + "/cancel")

                        .putMetadata("plan", plan)
                        .putMetadata("companyId", companyId.toString())

                        .setSubscriptionData(
                                SessionCreateParams.SubscriptionData.builder()
                                        .putMetadata("plan", plan)
                                        .putMetadata("companyId", companyId.toString())
                                        .build()
                        )

                        .addLineItem(
                                SessionCreateParams.LineItem.builder()
                                        .setPrice(priceId)
                                        .setQuantity(1L)
                                        .build()
                        )
                        .build();

        Session session = Session.create(params);

        return session.getUrl();
    }
}
