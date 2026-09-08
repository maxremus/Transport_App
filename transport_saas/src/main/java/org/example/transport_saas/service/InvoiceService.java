package org.example.transport_saas.service;

import lombok.RequiredArgsConstructor;
import org.example.transport_saas.entity.*;
import org.example.transport_saas.repository.ClientRepository;
import org.example.transport_saas.repository.CompanyRepository;
import org.example.transport_saas.repository.InvoiceRepository;
import org.example.transport_saas.repository.TripRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final TripRepository tripRepository;
    private final ClientRepository clientRepository;
    private final CompanyRepository companyRepository;

    public List<Invoice> getAllForCompany(Long companyId) {
        return invoiceRepository.findByCompanyIdOrderByIssueDateDesc(companyId);
    }

    public Invoice getIfBelongsToCompany(Long invoiceId, Long companyId) {
        Invoice invoice = invoiceRepository.findById(invoiceId).orElse(null);
        if (invoice == null || invoice.getCompany() == null
                || !invoice.getCompany().getId().equals(companyId)) {
            return null;
        }
        return invoice;
    }

    /**
     * Генерира фактура от избрани (все още нефактурирани) курсове на
     * даден клиент. Сумата на всеки ред е приходът (revenue) на курса -
     * това е сумата, която се таксува на клиента.
     */
    @Transactional
    public Invoice generateFromTrips(Long companyId, Long clientId, List<Long> tripIds, LocalDate dueDate) {

        Client client = clientRepository.findById(clientId).orElseThrow();
        if (!client.getCompany().getId().equals(companyId)) {
            throw new RuntimeException("Access denied");
        }

        Company company = companyRepository.findById(companyId).orElseThrow();

        List<Trip> trips = tripRepository.findByIdInAndCompanyId(tripIds, companyId);
        if (trips.isEmpty()) {
            throw new RuntimeException("Няма избрани курсове");
        }

        Invoice invoice = Invoice.builder()
                .invoiceNumber(nextInvoiceNumber(companyId))
                .issueDate(LocalDate.now())
                .dueDate(dueDate)
                .status(InvoiceStatus.ISSUED)
                .company(company)
                .client(client)
                .build();

        BigDecimal total = BigDecimal.ZERO;
        for (Trip trip : trips) {
            BigDecimal amount = trip.getRevenue() != null ? trip.getRevenue() : BigDecimal.ZERO;

            String desc = "Курс " + nullToEmpty(trip.getFromLocation()) + " - " + nullToEmpty(trip.getToLocation());
            if (trip.getTripDate() != null) {
                desc += " (" + trip.getTripDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")) + ")";
            }

            InvoiceItem item = InvoiceItem.builder()
                    .invoice(invoice)
                    .trip(trip)
                    .description(desc)
                    .amount(amount)
                    .build();

            invoice.getItems().add(item);
            total = total.add(amount);

            trip.setInvoiced(true);
        }

        invoice.setTotalAmount(total);

        return invoiceRepository.save(invoice);
    }

    public void markPaid(Long invoiceId, Long companyId) {
        Invoice invoice = getIfBelongsToCompany(invoiceId, companyId);
        if (invoice == null) {
            throw new RuntimeException("Access denied");
        }
        invoice.setStatus(InvoiceStatus.PAID);
        invoiceRepository.save(invoice);
    }

    @Transactional
    public void cancel(Long invoiceId, Long companyId) {
        Invoice invoice = getIfBelongsToCompany(invoiceId, companyId);
        if (invoice == null) {
            throw new RuntimeException("Access denied");
        }
        invoice.setStatus(InvoiceStatus.CANCELLED);
        // освобождаваме курсовете, за да могат да бъдат включени в нова фактура
        for (InvoiceItem item : invoice.getItems()) {
            if (item.getTrip() != null) {
                item.getTrip().setInvoiced(false);
            }
        }
        invoiceRepository.save(invoice);
    }

    private String nextInvoiceNumber(Long companyId) {
        long count = invoiceRepository.countByCompanyId(companyId);
        int year = LocalDate.now().getYear();
        return year + "-" + String.format("%04d", count + 1);
    }

    private String nullToEmpty(String s) {
        return s != null ? s : "";
    }
}
