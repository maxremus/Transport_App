package org.example.transport_saas.service;

import lombok.RequiredArgsConstructor;
import org.example.transport_saas.entity.Company;
import org.example.transport_saas.entity.User;
import org.example.transport_saas.entity.VehicleDocument;
import org.example.transport_saas.repository.CompanyRepository;
import org.example.transport_saas.repository.UserRepository;
import org.example.transport_saas.repository.VehicleDocumentRepository;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * Ежедневна проверка за изтичащ пробен период / абонамент и изтичащи
 * документи (на МПС и шофьори). Праща имейл само на потребителите,
 * които имат включено "Известия по имейл" в /settings.
 *
 * Ако SMTP не е конфигуриран (MAIL_USERNAME/MAIL_PASSWORD липсват),
 * грешката се хваща тихо и се логва в конзолата - същия подход, както
 * при PasswordResetService, за да не чупи стартирането на приложението.
 *
 * Документите на шофьорите НЕ живеят в тази база - идват от отделния
 * driver-service през DriverIntegrationService (същия източник, който
 * таблото използва за "Изтичащи документи на шофьори").
 */
@Service
@RequiredArgsConstructor
public class NotificationEmailService {

    private static final int SUBSCRIPTION_ALERT_DAYS = 3;
    private static final int DOCUMENT_ALERT_DAYS = 7;

    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;
    private final VehicleDocumentRepository vehicleDocumentRepository;
    private final DriverIntegrationService driverIntegrationService;
    private final JavaMailSender mailSender;

    /**
     * Ръчно пускане на проверката за една фирма (бутон "Изпрати сега" в
     * Настройки) - използва се за тестване, без да се чака до 08:00.
     * Връща true, ако е имало какво да се съобщи (и е пратен имейл).
     */
    public boolean sendNowForCompany(Long companyId) {
        Company company = companyRepository.findById(companyId).orElseThrow();

        List<User> recipients = userRepository.findByCompanyIdAndEmailNotificationsTrue(company.getId());
        if (recipients.isEmpty()) {
            return false;
        }

        StringBuilder body = new StringBuilder();
        appendSubscriptionAlert(company, body);
        appendDocumentAlerts(company, body);

        if (body.isEmpty()) {
            return false;
        }

        String fullMessage = "Здравей,\n\nЕто твоите известия от Transport Manager за фирма \""
                + company.getName() + "\":\n\n" + body
                + "\nМожеш да изключиш тези известия по всяко време от Настройки.";

        for (User user : recipients) {
            if (user.getEmail() != null && !user.getEmail().isBlank()) {
                sendEmail(user.getEmail(), "Известия - Transport Manager", fullMessage);
            }
        }
        return true;
    }

    /**
     * Пуска се веднъж на ден в 08:00 (сървърно време).
     */
    @Scheduled(cron = "0 0 8 * * *")
    public void sendDailyAlerts() {
        for (Company company : companyRepository.findAll()) {
            sendNowForCompany(company.getId());
        }
    }

    private void appendSubscriptionAlert(Company company, StringBuilder body) {
        LocalDate today = LocalDate.now();

        if (company.getSubscriptionExpiry() != null) {
            long daysLeft = ChronoUnit.DAYS.between(today, company.getSubscriptionExpiry());
            if (daysLeft >= 0 && daysLeft <= SUBSCRIPTION_ALERT_DAYS) {
                body.append("⚠ Абонаментът ви (").append(company.getSubscriptionPlan())
                        .append(") изтича на ").append(company.getSubscriptionExpiry())
                        .append(" (остават ").append(daysLeft).append(" дни).\n");
            }
        } else if (company.getTrialEndsAt() != null) {
            long daysLeft = ChronoUnit.DAYS.between(today, company.getTrialEndsAt());
            if (daysLeft >= 0 && daysLeft <= SUBSCRIPTION_ALERT_DAYS) {
                body.append("⚠ Пробният ви период изтича на ").append(company.getTrialEndsAt())
                        .append(" (остават ").append(daysLeft).append(" дни).\n");
            }
        }
    }

    private void appendDocumentAlerts(Company company, StringBuilder body) {
        LocalDate alertDate = LocalDate.now().plusDays(DOCUMENT_ALERT_DAYS);

        List<VehicleDocument> vehicleDocs = vehicleDocumentRepository.findExpiringDocuments(company.getId(), alertDate);
        for (VehicleDocument doc : vehicleDocs) {
            body.append("🚛 Документ ").append(doc.getType())
                    .append(" на МПС ").append(doc.getVehicle().getRegistrationNumber())
                    .append(" изтича на ").append(doc.getExpiryDate()).append(".\n");
        }

        // Документите на шофьорите живеят в driver-service, не тук -
        // ползваме същия интеграционен слой, който пълни таблото.
        List<org.example.transport_saas.DTO.DriverDocumentRequestDTO> driverDocs;
        try {
            driverDocs = driverIntegrationService.getExpiringDocumentsForCompany(company.getId());
        } catch (Exception e) {
            System.out.println("⚠ driver-service недостъпен при генериране на известия за фирма "
                    + company.getId() + ": " + e.getMessage());
            driverDocs = List.of();
        }

        for (org.example.transport_saas.DTO.DriverDocumentRequestDTO doc : driverDocs) {
            if (doc.getExpiryDate() == null || doc.getExpiryDate().isAfter(alertDate)) {
                continue; // driver-service връща до 30 дни - тук филтрираме до нашия по-кратък праг
            }
            body.append("🪪 Документ ").append(doc.getType())
                    .append(" на шофьор ").append(doc.getDriverName())
                    .append(" изтича на ").append(doc.getExpiryDate()).append(".\n");
        }
    }

    private void sendEmail(String toEmail, String subject, String text) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(toEmail);
            message.setSubject(subject);
            message.setText(text);
            mailSender.send(message);
        } catch (Exception e) {
            System.out.println("⚠ Изпращането на известие се провали (SMTP неконфигуриран?) до " + toEmail + ": " + e.getMessage());
        }
    }
}
