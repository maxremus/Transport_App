package org.example.driverservice.controller;

import org.example.driverservice.DTO.DriverDocumentRequestDTO;
import org.example.driverservice.entity.DriverDocument;
import org.example.driverservice.service.DriverDocumentService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/driver-documents")
public class DriverDocumentController {

    private final DriverDocumentService driverDocumentService;

    @Value("${app.internal-key}")
    private String internalKey;

    public DriverDocumentController(DriverDocumentService driverDocumentService) {
        this.driverDocumentService = driverDocumentService;
    }

    private void validateKey(String apiKey) {
        if (!internalKey.equals(apiKey)) {
            throw new RuntimeException("Unauthorized");
        }
    }

    @GetMapping
    public List<DriverDocument> getForDriver(@RequestParam Long driverId,
                                              @RequestHeader("X-API-KEY") String apiKey) {
        validateKey(apiKey);
        return driverDocumentService.getForDriver(driverId);
    }

    @GetMapping("/company")
    public List<DriverDocument> getForCompany(@RequestParam Long companyId,
                                               @RequestHeader("X-API-KEY") String apiKey) {
        validateKey(apiKey);
        return driverDocumentService.getForCompany(companyId);
    }

    @PostMapping
    public DriverDocument save(@RequestBody DriverDocumentRequestDTO req,
                                @RequestHeader("X-API-KEY") String apiKey) {
        validateKey(apiKey);

        return driverDocumentService.create(
                req.getDriverId(),
                req.getType(),
                req.getExpiryDate(),
                req.getNumber()
        );
    }

    @PutMapping("/{id}")
    public DriverDocument update(@PathVariable Long id,
                                  @RequestBody DriverDocumentRequestDTO req,
                                  @RequestHeader("X-API-KEY") String apiKey) {
        validateKey(apiKey);

        return driverDocumentService.update(
                id,
                req.getType(),
                req.getExpiryDate(),
                req.getNumber()
        );
    }
}
