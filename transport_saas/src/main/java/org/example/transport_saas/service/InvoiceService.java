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

    /** Фактури с минал падеж, които все още не са платени - за известие. */
    public List<Invoice> getOverdueInvoices(Long companyId) {
        return invoiceRepository.findByCompanyIdAndStatusAndDueDateBefore(
                companyId, InvoiceStatus.ISSUED, LocalDate.now());
    }

    /** Обща неплатена сума (с ДДС) за даден клиент - "картон на клиента". */
    public BigDecimal getUnpaidBalanceForClient(Long companyId, Long clientId) {
        return getAllForCompany(companyId).stream()
                .filter(inv -> inv.getClient() != null && inv.getClient().getId().equals(clientId))
                .filter(inv -> inv.getStatus() == InvoiceStatus.ISSUED)
                .map(Invoice::getGrandTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
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
    public Invoice generateFromTrips(Long companyId, Long clientId, List<Long> tripIds,
                                      LocalDate dueDate, BigDecimal vatRateOverride) {

        Client client = clientRepository.findById(clientId).orElseThrow();
        if (!client.getCompany().getId().equals(companyId)) {
            throw new RuntimeException("Access denied");
        }

        Company company = companyRepository.findById(companyId).orElseThrow();

        List<Trip> trips = tripRepository.findByIdInAndCompanyId(tripIds, companyId);
        if (trips.isEmpty()) {
            throw new RuntimeException("Няма избрани курсове");
        }

        // ако фирмата не е регистрирана по ДДС, не може да начислява ДДС на
        // фактурата, независимо какво е подадено от формата
        BigDecimal vatRate = company.isVatRegistered()
                ? (vatRateOverride != null ? vatRateOverride : BigDecimal.valueOf(20))
                : BigDecimal.ZERO;

        Invoice invoice = Invoice.builder()
                .invoiceNumber(nextInvoiceNumber(company))
                .issueDate(LocalDate.now())
                .dueDate(dueDate)
                .status(InvoiceStatus.ISSUED)
                .vatRate(vatRate)
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

    /**
     * Редакция на вече издадена фактура - ако е допусната грешка
     * (грешна сума, описание, падеж и т.н.), без да се налага анулиране.
     * Сумата на фактурата се преизчислява от редовете след промяната.
     */
    @Transactional
    public void update(Long invoiceId, Long companyId, String invoiceNumber, LocalDate dueDate, String notes,
                        BigDecimal vatRate, List<Long> itemIds, List<String> descriptions,
                        List<BigDecimal> amounts) {

        Invoice invoice = getIfBelongsToCompany(invoiceId, companyId);
        if (invoice == null) {
            throw new RuntimeException("Access denied");
        }

        // Номерът може да се смени ръчно (напр. за да продължи номерацията
        // от стара система), но трябва да остане уникален за фирмата.
        if (invoiceNumber != null && !invoiceNumber.isBlank()
                && !invoiceNumber.equals(invoice.getInvoiceNumber())) {

            boolean taken = invoiceRepository.existsByCompanyIdAndInvoiceNumberAndIdNot(
                    companyId, invoiceNumber, invoiceId);
            if (taken) {
                throw new IllegalArgumentException("Вече има фактура с номер " + invoiceNumber + ".");
            }
            invoice.setInvoiceNumber(invoiceNumber);
        }

        invoice.setDueDate(dueDate);
        invoice.setNotes(notes);

        // ако фирмата не е регистрирана по ДДС, не позволяваме начисляване
        if (!invoice.getCompany().isVatRegistered()) {
            vatRate = BigDecimal.ZERO;
        }
        invoice.setVatRate(vatRate != null ? vatRate : BigDecimal.ZERO);

        BigDecimal total = BigDecimal.ZERO;
        for (int i = 0; i < itemIds.size(); i++) {
            Long itemId = itemIds.get(i);
            String desc = descriptions.get(i);
            BigDecimal amount = amounts.get(i) != null ? amounts.get(i) : BigDecimal.ZERO;

            for (InvoiceItem item : invoice.getItems()) {
                if (item.getId().equals(itemId)) {
                    item.setDescription(desc);
                    item.setAmount(amount);
                    break;
                }
            }
            total = total.add(amount);
        }

        invoice.setTotalAmount(total);

        invoiceRepository.save(invoice);
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

    private String nextInvoiceNumber(Company company) {
        long number = company.getNextInvoiceNumberOrDefault();
        company.setNextInvoiceNumber(number + 1);
        companyRepository.save(company);
        return String.format("%010d", number);
    }

    private String nullToEmpty(String s) {
        return s != null ? s : "";
    }
}
