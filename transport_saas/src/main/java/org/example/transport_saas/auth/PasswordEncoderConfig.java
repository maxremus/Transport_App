package org.example.transport_saas.auth;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * PasswordEncoder е изваден в собствен, независим @Configuration клас,
 * защото SecurityConfig инжектира UserService, а UserService на свой ред
 * се нуждае от PasswordEncoder - ако бинът стои в SecurityConfig, се получава
 * кръгова зависимост (SecurityConfig -> UserService -> PasswordEncoder ->
 * SecurityConfig), която Spring отказва да резолвне при стартиране.
 */
@Configuration
public class PasswordEncoderConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
