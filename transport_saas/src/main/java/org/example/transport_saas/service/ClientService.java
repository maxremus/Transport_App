package org.example.transport_saas.service;

import lombok.RequiredArgsConstructor;
import org.example.transport_saas.entity.Client;
import org.example.transport_saas.entity.Company;
import org.example.transport_saas.repository.ClientRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClientService {

    private final ClientRepository clientRepository;

    public List<Client> getAllForCompany(Long companyId) {
        return clientRepository.findByCompanyIdOrderByNameAsc(companyId);
    }

    public Client getIfBelongsToCompany(Long clientId, Long companyId) {
        Client client = clientRepository.findById(clientId).orElse(null);
        if (client == null || client.getCompany() == null
                || !client.getCompany().getId().equals(companyId)) {
            return null;
        }
        return client;
    }

    public void save(Client client, Long companyId) {
        client.setCompany(Company.builder().id(companyId).build());
        clientRepository.save(client);
    }

    public void update(Long clientId, Long companyId, Client updated) {
        Client client = getIfBelongsToCompany(clientId, companyId);
        if (client == null) {
            throw new RuntimeException("Access denied");
        }

        client.setName(updated.getName());
        client.setBulstat(updated.getBulstat());
        client.setContactPerson(updated.getContactPerson());
        client.setPhone(updated.getPhone());
        client.setEmail(updated.getEmail());
        client.setAddress(updated.getAddress());
        client.setNotes(updated.getNotes());

        clientRepository.save(client);
    }

    public void delete(Long clientId, Long companyId) {
        Client client = getIfBelongsToCompany(clientId, companyId);
        if (client == null) {
            throw new RuntimeException("Access denied");
        }
        clientRepository.delete(client);
    }
}
