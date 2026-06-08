package org.example.transport_saas.exception;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler extends RuntimeException {

    @ExceptionHandler(SubscriptionException.class)
    public String handleSubscriptionException(
            SubscriptionException ex,
            Model model) {

        model.addAttribute("errorMessage", ex.getMessage());
        return "subscription-expired";
    }

    @ExceptionHandler(PlanLimitException.class)
    public String handlePlanLimit(
            PlanLimitException ex,
            Model model) {

        model.addAttribute("errorMessage", ex.getMessage());
        return "upgrade";
    }

    @ExceptionHandler(PlanLimitExceededException.class)
    public String handlePlanLimitExceeded(
            PlanLimitExceededException ex,
            Model model) {

        model.addAttribute("errorMessage", ex.getMessage());

        return "upgrade-required";
    }
}
