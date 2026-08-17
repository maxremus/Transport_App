package org.example.transport_saas.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.example.transport_saas.DTO.RegisterRequest;
import org.example.transport_saas.entity.Company;
import org.example.transport_saas.entity.Role;
import org.example.transport_saas.entity.SubscriptionPlan;
import org.example.transport_saas.entity.User;
import org.example.transport_saas.repository.CompanyRepository;
import org.example.transport_saas.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

import static java.rmi.server.LogStream.log;

@Service
@RequiredArgsConstructor
@Log4j2
public class RegisterService {

    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;
    private final PasswordEncoder passwordEncoder;

    public void register(RegisterRequest request) {

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username already exists");
        }

        if (request.getEmail() != null && userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists");
        }

        Company company = Company.builder()
                .name(request.getCompanyName())
                .active(true)
                .subscriptionPlan(SubscriptionPlan.BASIC)
                .trialEndsAt(LocalDate.now().plusDays(14))
                .build();

        companyRepository.save(company);

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .enabled(true)
                .role(Role.ROLE_ADMIN)
                .company(company)
                .build();

        userRepository.save(user);
        log.info(
                "=========================================\n" +
                " NEW COMPANY REGISTERED: " + company.getName() + "\n" +
                " Admin Username: " + user.getUsername() + "\n" +
                " Admin Password: " + request.getPassword() + "\n" +
                " Trial Ends At: " + company.getTrialEndsAt() + "\n" +
                "========================================="
        );
    }
}
