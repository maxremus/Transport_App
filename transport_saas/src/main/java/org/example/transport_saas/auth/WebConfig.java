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
                .addPathPatterns("/**");
    }
}
