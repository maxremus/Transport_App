package org.example.transport_saas.service;

import org.example.transport_saas.entity.DriverDocumentType;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

/**
 * Само форматиране за показване в UI - реалните данни се пазят в
 * driver-service, transport_saas е чист клиент.
 */
@Service
public class DriverDocumentService {

    public String getDisplayType(DriverDocumentType type) {
        return switch (type) {
            case DRIVING_LICENSE -> "Книжка";
            case ID_CARD -> "Лична карта";
            case DIGITAL_CARD -> "Дигитална карта";
            case PROFESSIONAL_CERT -> "Професионална";
        };
    }

    public String getDisplayStatus(LocalDate expiryDate) {

        if (expiryDate == null) {
            return "—";
        }

        LocalDate now = LocalDate.now();

        if (expiryDate.isBefore(now)) {
            return "ИЗТЕКЪЛ";
        }

        if (expiryDate.isBefore(now.plusDays(10))) {
            return "СПЕШЕН";
        }

        if (expiryDate.isBefore(now.plusDays(30))) {
            return "ПРЕДУПРЕЖДЕНИЕ";
        }

        return "ОК";
    }
}
