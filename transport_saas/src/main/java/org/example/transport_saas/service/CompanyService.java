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

    public void updateProfile(Long companyId, String name, String bulstat, String address,
                               String iban, String mol, String vatNumber, boolean vatRegistered) {
        Company company = getById(companyId);
        company.setName(name);
        company.setBulstat(bulstat);
        company.setAddress(address);
        company.setIban(iban);
        company.setMol(mol);
        company.setVatNumber(vatNumber);
        company.setVatRegistered(vatRegistered);
        companyRepository.save(company);
    }

    public void updateNextInvoiceNumber(Long companyId, long nextInvoiceNumber) {
        Company company = getById(companyId);
        company.setNextInvoiceNumber(nextInvoiceNumber);
        companyRepository.save(company);
    }
}
