package org.example.transport_saas.exception;

public class PlanLimitException extends RuntimeException {
    public PlanLimitException(String message) {
        super(message);
    }
}
