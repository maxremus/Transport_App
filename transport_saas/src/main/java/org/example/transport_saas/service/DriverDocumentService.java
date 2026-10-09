package org.example.transport_saas.service;

import lombok.RequiredArgsConstructor;
import org.example.transport_saas.entity.DriverDocumentType;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

/**
 * Само форматиране за показване в UI - реалните данни се пазят в
 * driver-service, transport_saas е чист клиент.
 */
@Service
@RequiredArgsConstructor
public class DriverDocumentService {

    private final MessageSource messageSource;

    private String msg(String key) {
        return messageSource.getMessage(key, null, LocaleContextHolder.getLocale());
    }

    public String getDisplayType(DriverDocumentType type) {
        return switch (type) {
            case DRIVING_LICENSE -> msg("doc.type.driving_license");
            case ID_CARD -> msg("doc.type.id_card");
            case DIGITAL_CARD -> msg("doc.type.digital_card");
            case PROFESSIONAL_CERT -> msg("doc.type.professional_cert");
        };
    }

    /** Суров, locale-независим статус - за CSS/логика в шаблоните (НЕ за показване). */
    public String getStatus(LocalDate expiryDate) {

        if (expiryDate == null) {
            return "NONE";
        }

        LocalDate now = LocalDate.now();

        if (expiryDate.isBefore(now)) {
            return "EXPIRED";
        }

        if (expiryDate.isBefore(now.plusDays(10))) {
            return "URGENT";
        }

        if (expiryDate.isBefore(now.plusDays(30))) {
            return "WARNING";
        }

        return "OK";
    }

    public String getDisplayStatus(LocalDate expiryDate) {
        return switch (getStatus(expiryDate)) {
            case "NONE" -> "—";
            case "EXPIRED" -> msg("doc.status.expired");
            case "URGENT" -> msg("doc.status.urgent");
            case "WARNING" -> msg("doc.status.warning");
            default -> msg("doc.status.ok");
        };
    }
}
