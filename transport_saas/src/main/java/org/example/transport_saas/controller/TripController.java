package org.example.transport_saas.controller;

import lombok.RequiredArgsConstructor;
import org.example.transport_saas.auth.SecurityUtils;
import org.example.transport_saas.entity.Trip;
import org.example.transport_saas.service.ClientService;
import org.example.transport_saas.service.TripExportService;
import org.example.transport_saas.service.TripService;
import org.example.transport_saas.service.VehicleService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Controller
@RequiredArgsConstructor
@RequestMapping("/trips")
public class TripController {

    private final TripService tripService;
    private final VehicleService vehicleService;
    private final ClientService clientService;
    private final TripExportService tripExportService;

    @GetMapping
    public String list(@RequestParam(required = false) Long editId, Model model) {

        Long companyId = SecurityUtils.getCurrentCompanyId();

        model.addAttribute("trips",
                tripService.getAllForCompany(companyId));

        model.addAttribute("vehicles",
                vehicleService.getAllForCompany(companyId));

        model.addAttribute("clients",
                clientService.getAllForCompany(companyId));

        Trip trip = null;
        if (editId != null) {
            trip = tripService.getIfBelongsToCompany(editId, companyId);
        }
        if (trip == null) {
            trip = new Trip();
        }

        model.addAttribute("trip", trip);
        model.addAttribute("editId", (trip.getId() != null) ? trip.getId() : null);

        return "trips";
    }

    @PostMapping
    public String add(@ModelAttribute Trip trip,
                      @RequestParam Long vehicleId,
                      @RequestParam(required = false) Long clientId) {

        Long companyId = SecurityUtils.getCurrentCompanyId();

        tripService.save(trip, companyId, vehicleId, clientId);

        return "redirect:/trips";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id,
                         @ModelAttribute Trip trip,
                         @RequestParam Long vehicleId,
                         @RequestParam(required = false) Long clientId) {

        Long companyId = SecurityUtils.getCurrentCompanyId();

        tripService.update(id, companyId, trip, vehicleId, clientId);

        return "redirect:/trips";
    }

    @GetMapping("/export")
    public ResponseEntity<byte[]> export() {

        Long companyId = SecurityUtils.getCurrentCompanyId();
        var trips = tripService.getAllForCompany(companyId);

        byte[] excel = tripExportService.exportTrips(trips);

        String filename = "kursove_" +
                LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + ".xlsx";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.parseMediaType(
                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(excel);
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {

        Long companyId = SecurityUtils.getCurrentCompanyId();

        tripService.delete(id, companyId);

        return "redirect:/trips";
    }
}
