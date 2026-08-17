package org.example.transport_saas.service;

import lombok.RequiredArgsConstructor;
import org.example.transport_saas.entity.PasswordResetToken;
import org.example.transport_saas.entity.User;
import org.example.transport_saas.repository.PasswordResetTokenRepository;
import org.example.transport_saas.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PasswordResetService {

    private final UserRepository userRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JavaMailSender mailSender;

    @Value("${app.frontend-public-url:}")
    private String publicUrl;

    /**
     * Ако потребител с този имейл съществува, генерира токен и праща линк.
     * Винаги "успява" тихо (дори ако имейлът не съществува), за да не се
     * издава кои имейли са регистрирани в системата.
     */
    public void requestReset(String email) {

        userRepository.findByEmail(email).ifPresent(user -> {

            String token = UUID.randomUUID().toString();

            PasswordResetToken resetToken = PasswordResetToken.builder()
                    .token(token)
                    .user(user)
                    .expiresAt(LocalDateTime.now().plusHours(1))
                    .used(false)
                    .build();

            tokenRepository.save(resetToken);

            String base = (publicUrl != null && !publicUrl.isBlank())
                    ? publicUrl
                    : "http://localhost:8080";

            String link = base + "/reset-password?token=" + token;

            sendResetEmail(user.getEmail(), link);
        });
    }

    private void sendResetEmail(String toEmail, String link) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(toEmail);
            message.setSubject("Възстановяване на парола - Transport Manager");
            message.setText(
                    "Здравей,\n\n" +
                    "Получихме заявка за възстановяване на паролата ти.\n" +
                    "Кликни на линка по-долу, за да зададеш нова парола (валиден 1 час):\n\n" +
                    link + "\n\n" +
                    "Ако не си правил тази заявка, просто игнорирай този имейл."
            );
            mailSender.send(message);
        } catch (Exception e) {
            // Ако SMTP не е конфигуриран (MAIL_USERNAME/MAIL_PASSWORD),
            // логваме линка в конзолата, за да работи функцията и без имейл.
            System.out.println("⚠ Изпращането на имейл се провали (SMTP неконфигуриран?). " +
                    "Линк за възстановяване на парола за " + toEmail + ": " + link);
        }
    }

    public boolean isTokenValid(String token) {
        return tokenRepository.findByToken(token)
                .filter(t -> !t.isUsed())
                .filter(t -> t.getExpiresAt().isAfter(LocalDateTime.now()))
                .isPresent();
    }

    /**
     * @return true ако паролата е сменена успешно
     */
    public boolean resetPassword(String token, String newPassword) {

        var resetTokenOpt = tokenRepository.findByToken(token)
                .filter(t -> !t.isUsed())
                .filter(t -> t.getExpiresAt().isAfter(LocalDateTime.now()));

        if (resetTokenOpt.isEmpty()) {
            return false;
        }

        PasswordResetToken resetToken = resetTokenOpt.get();
        User user = resetToken.getUser();

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        resetToken.setUsed(true);
        tokenRepository.save(resetToken);

        return true;
    }
}
