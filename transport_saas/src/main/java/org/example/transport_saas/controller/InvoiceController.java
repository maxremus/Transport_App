package org.example.transport_saas.controller;

import lombok.RequiredArgsConstructor;
import org.example.transport_saas.auth.SecurityUtils;
import org.example.transport_saas.entity.Invoice;
import org.example.transport_saas.repository.TripRepository;
import org.example.transport_saas.service.ClientService;
import org.example.transport_saas.service.CompanyService;
import org.example.transport_saas.service.InvoicePdfService;
import org.example.transport_saas.service.InvoiceService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/invoices")
public class InvoiceController {

    private final InvoiceService invoiceService;
    private final InvoicePdfService invoicePdfService;
    private final ClientService clientService;
    private final CompanyService companyService;
    private final TripRepository tripRepository;

    @GetMapping
    public String list(@RequestParam(required = false) Long clientId, Model model) {

        Long companyId = SecurityUtils.getCurrentCompanyId();

        model.addAttribute("invoices", invoiceService.getAllForCompany(companyId));
        model.addAttribute("clients", clientService.getAllForCompany(companyId));
        model.addAttribute("overdueInvoices", invoiceService.getOverdueInvoices(companyId));
        model.addAttribute("vatRegistered", companyService.getById(companyId).isVatRegistered());

        // ако е избран клиент, показваме неговите нефактурирани курсове,
        // за да може да се генерира нова фактура директно от тук
        model.addAttribute("selectedClientId", clientId);
        if (clientId != null) {
            model.addAttribute("uninvoicedTrips",
                    tripRepository.findByCompanyIdAndClientIdAndInvoicedFalseOrderByTripDateAsc(companyId, clientId));
        }

        return "invoices";
    }

    @PostMapping("/generate")
    public String generate(@RequestParam Long clientId,
                            @RequestParam List<Long> tripIds,
                            @RequestParam(required = false) String dueDate,
                            @RequestParam(required = false) java.math.BigDecimal vatRate) {

        Long companyId = SecurityUtils.getCurrentCompanyId();

        LocalDate due = (dueDate != null && !dueDate.isBlank())
                ? LocalDate.parse(dueDate)
                : LocalDate.now().plusDays(14);

        invoiceService.generateFromTrips(companyId, clientId, tripIds, due, vatRate);

        return "redirect:/invoices";
    }

    @PostMapping("/{id}/paid")
    public String markPaid(@PathVariable Long id) {
        Long companyId = SecurityUtils.getCurrentCompanyId();
        invoiceService.markPaid(id, companyId);
        return "redirect:/invoices";
    }

    @PostMapping("/{id}/cancel")
    public String cancel(@PathVariable Long id) {
        Long companyId = SecurityUtils.getCurrentCompanyId();
        invoiceService.cancel(id, companyId);
        return "redirect:/invoices";
    }

    @GetMapping("/{id}/pdf")
    public ResponseEntity<byte[]> pdf(@PathVariable Long id) {

        Long companyId = SecurityUtils.getCurrentCompanyId();
        Invoice invoice = invoiceService.getIfBelongsToCompany(id, companyId);
        if (invoice == null) {
            return ResponseEntity.notFound().build();
        }

        byte[] pdf = invoicePdfService.generatePdf(invoice);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"faktura_" + invoice.getInvoiceNumber() + ".pdf\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }
}
