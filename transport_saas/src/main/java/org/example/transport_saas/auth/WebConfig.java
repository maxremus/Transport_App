package org.example.transport_saas.auth;

import lombok.RequiredArgsConstructor;
import org.example.transport_saas.util.SubscriptionInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final SubscriptionInterceptor subscriptionInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor((HandlerInterceptor) subscriptionInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(
                        // страници за подновяване/плащане на абонамента - трябва да
                        // са достъпни дори когато абонаментът вече е изтекъл,
                        // иначе потребителят никога не може да плати отново
                        "/upgrade", "/upgrade/**",
                        "/success", "/cancel",
                        // публични/автентикационни страници
                        "/", "/login", "/logout", "/register",
                        "/forgot-password", "/reset-password",
                        "/subscription-expired",
                        // статични ресурси
                        "/css/**", "/js/**", "/images/**"
                );
    }
}
