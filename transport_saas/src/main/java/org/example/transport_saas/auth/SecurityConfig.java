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

                // CSRF protection е ВКЛЮЧЕНА (по подразбиране в Spring Security)
                // за всички браузърни/сесийни форми.
                //
                // /api/v1/company/** е изключение - това е чист server-to-server
                // REST API (извикван от payment-service през Feign), защитен със
                // собствен X-API-KEY механизъм, не с браузърна сесия. Feign не
                // изпраща CSRF токен, така че без това изключение CSRF филтърът
                // отхвърля заявката -> пренасочва към /login -> Java-клиентът на
                // Feign (HttpURLConnection) не сменя POST->GET при redirect ->
                // POST-ва пак към /login -> /login също изисква CSRF за POST ->
                // безкраен redirect loop до 20-те опита на Java.
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers("/api/v1/company/**")
                )

                .sessionManagement(session -> session
                        .sessionFixation().migrateSession()
                )

                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/login", "/register", "/css/**", "/images/**").permitAll()

                        // Stripe пренасочва браузъра директно тук след плащане -
                        // трябва да са достъпни независимо дали сесията е още валидна
                        .requestMatchers("/success", "/cancel").permitAll()

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
