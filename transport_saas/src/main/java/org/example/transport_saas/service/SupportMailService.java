package org.example.transport_saas.service;

import lombok.RequiredArgsConstructor;
import org.example.transport_saas.entity.Company;
import org.example.transport_saas.entity.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SupportMailService {

    private final JavaMailSender mailSender;

    @Value("${app.support-email:admintransportapp@gmail.com}")
    private String adminEmail;

    public void sendToAdmin(Company company, User user, String subject, String message) {
        try {
            SimpleMailMessage mail = new SimpleMailMessage();
            mail.setTo(adminEmail);
            mail.setSubject("[Support] " + subject);
            mail.setText(
                    "Фирма: " + company.getName() + " (ID: " + company.getId() + ")\n" +
                    "Потребител: " + user.getUsername() + "\n" +
                    "Имейл за връзка: " + (user.getEmail() != null ? user.getEmail() : "(няма зададен)") + "\n\n" +
                    "Съобщение:\n" + message
            );
            if (user.getEmail() != null && !user.getEmail().isBlank()) {
                mail.setReplyTo(user.getEmail());
            }
            mailSender.send(mail);
        } catch (Exception e) {
            System.out.println("⚠ Изпращането на съобщение до администратора се провали (SMTP неконфигуриран?): " + e.getMessage());
        }
    }
}
