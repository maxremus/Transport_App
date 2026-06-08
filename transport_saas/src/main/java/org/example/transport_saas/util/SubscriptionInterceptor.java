package org.example.transport_saas.util;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.example.transport_saas.auth.SecurityUtils;
import org.example.transport_saas.service.SubscriptionService;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;


@Component
@RequiredArgsConstructor
public class SubscriptionInterceptor implements HandlerInterceptor {

    private final SubscriptionService subscriptionService;

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {

        if (request.getUserPrincipal() == null) {
            return true;
        }

        Long companyId = SecurityUtils.getCurrentCompanyId();
        subscriptionService.validateSubscription(companyId);

        return true;
    }
}
