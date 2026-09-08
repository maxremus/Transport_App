package org.example.transport_saas.controller;

import lombok.RequiredArgsConstructor;
import org.example.transport_saas.auth.SecurityUtils;
import org.example.transport_saas.entity.Client;
import org.example.transport_saas.service.ClientService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/clients")
public class ClientController {

    private final ClientService clientService;

    @GetMapping
    public String list(@RequestParam(required = false) Long editId, Model model) {

        Long companyId = SecurityUtils.getCurrentCompanyId();

        model.addAttribute("clients", clientService.getAllForCompany(companyId));

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
