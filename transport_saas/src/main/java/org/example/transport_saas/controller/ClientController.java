package org.example.transport_saas.controller;

import lombok.RequiredArgsConstructor;
import org.example.transport_saas.auth.SecurityUtils;
import org.example.transport_saas.entity.Client;
import org.example.transport_saas.service.ClientService;
import org.example.transport_saas.service.InvoiceService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@RequestMapping("/clients")
public class ClientController {

    private final ClientService clientService;
    private final InvoiceService invoiceService;

    @GetMapping
    public String list(@RequestParam(required = false) Long editId, Model model) {

        Long companyId = SecurityUtils.getCurrentCompanyId();

        List<Client> clients = clientService.getAllForCompany(companyId);
        model.addAttribute("clients", clients);

        // "картон на клиента" - колко му дължат неплатени фактури, за
        // бърз преглед кой клиент трябва да се подсети за плащане
        Map<Long, BigDecimal> balances = new HashMap<>();
        for (Client c : clients) {
            balances.put(c.getId(), invoiceService.getUnpaidBalanceForClient(companyId, c.getId()));
        }
        model.addAttribute("balances", balances);

        Client client = null;
        if (editId != null) {
            client = clientService.getIfBelongsToCompany(editId, companyId);
        }
        if (client == null) {
            client = new Client();
        }

        model.addAttribute("client", client);
        model.addAttribute("editId", client.getId());

        return "clients";
    }

    @PostMapping
    public String add(@ModelAttribute Client client) {
        Long companyId = SecurityUtils.getCurrentCompanyId();
        clientService.save(client, companyId);
        return "redirect:/clients";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id, @ModelAttribute Client client) {
        Long companyId = SecurityUtils.getCurrentCompanyId();
        clientService.update(id, companyId, client);
        return "redirect:/clients";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        Long companyId = SecurityUtils.getCurrentCompanyId();
        clientService.delete(id, companyId);
        return "redirect:/clients";
    }
}
