package org.example.transport_saas.auth;

import lombok.RequiredArgsConstructor;
import org.example.transport_saas.service.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final UserService userService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http

                // CSRF protection е ВКЛЮЧЕНА (по подразбиране в Spring Security).
                // Stripe webhook-ът НЕ живее тук - той е в payment-service,
                // отделно Spring Boot приложение без spring-security изобщо,
                // така че не му трябва CSRF exemption в тази услуга.
                // Всички форми в темплейтите вече очакват _csrf.token/_csrf.parameterName.

                .sessionManagement(session -> session
                        .sessionFixation().migrateSession()
                )

                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/login", "/register", "/css/**", "/images/**").permitAll()

                        // Stripe пренасочва браузъра директно тук след плащане -
                        // трябва да са достъпни независимо дали сесията е още валидна
                        .requestMatchers("/success", "/cancel").permitAll()

                        // 🔥 ТОВА РЕШАВА ПРОБЛЕМА
                        .requestMatchers("/api/v1/company/**").permitAll()

                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )

                .userDetailsService(userService)

                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/dashboard", true)
                        .permitAll()
                )

                .logout(logout -> logout
                        .logoutSuccessUrl("/")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                );

        return http.build();
    }
}
