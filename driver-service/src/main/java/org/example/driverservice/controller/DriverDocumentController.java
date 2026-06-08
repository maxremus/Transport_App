package org.example.driverservice.controller;

import org.example.driverservice.DTO.DriverDocumentRequestDTO;
import org.example.driverservice.service.DriverDocumentService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/driver-documents")
public class DriverDocumentController {

    private final DriverDocumentService driverDocumentService;

    public DriverDocumentController(DriverDocumentService driverDocumentService) {
        this.driverDocumentService = driverDocumentService;
    }

    @PostMapping
    public void save(@RequestBody DriverDocumentRequestDTO req) {

        driverDocumentService.saveOrUpdate(
                req.getDriverId(),
                req.getType(),
                req.getExpiryDate(),
                req.getNumber()
        );
    }
}
