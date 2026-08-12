package org.example.transport_saas.service;

import lombok.RequiredArgsConstructor;
import org.example.transport_saas.entity.Company;
import org.example.transport_saas.repository.CompanyRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CompanyService {

    private final CompanyRepository companyRepository;

    public Company getById(Long companyId) {
        return companyRepository.findById(companyId).orElseThrow();
    }

    public void updateProfile(Long companyId, String name, String bulstat) {
        Company company = getById(companyId);
        company.setName(name);
        company.setBulstat(bulstat);
        companyRepository.save(company);
    }
}
