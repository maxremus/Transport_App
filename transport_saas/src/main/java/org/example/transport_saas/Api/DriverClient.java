package org.example.transport_saas.Api;

import java.util.List;

import org.example.transport_saas.DTO.DriverCreateDTO;
import org.example.transport_saas.DTO.DriverDTO;
import org.example.transport_saas.DTO.DriverDocumentRequestDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(
        name = "driver-service",
        url = "${driver.service.url}"
)
public interface DriverClient {

    @GetMapping("/api/v1/drivers")
    List<DriverDTO> getAllDrivers(@RequestParam("companyId") Long companyId,
                                   @RequestHeader("X-API-KEY") String apiKey);

    @GetMapping("/api/v1/drivers/{id}")
    DriverDTO getDriver(@PathVariable("id") Long id,
                         @RequestParam("companyId") Long companyId,
                         @RequestHeader("X-API-KEY") String apiKey);

    @PostMapping("/api/v1/drivers")
    DriverDTO createDriver(@RequestBody DriverCreateDTO dto,
                            @RequestHeader("X-API-KEY") String apiKey);

    @PutMapping("/api/v1/drivers/{id}")
    DriverDTO updateDriver(@PathVariable("id") Long id,
                           @RequestParam("companyId") Long companyId,
                           @RequestBody DriverCreateDTO dto,
                           @RequestHeader("X-API-KEY") String apiKey);

    @GetMapping("/api/v1/driver-documents")
    List<DriverDocumentRequestDTO> getDocuments(@RequestParam("driverId") Long driverId,
                                                 @RequestHeader("X-API-KEY") String apiKey);

    @GetMapping("/api/v1/driver-documents/company")
    List<DriverDocumentRequestDTO> getDocumentsForCompany(@RequestParam("companyId") Long companyId,
                                                           @RequestHeader("X-API-KEY") String apiKey);

    @PostMapping("/api/v1/driver-documents")
    DriverDocumentRequestDTO createDocument(@RequestBody DriverDocumentRequestDTO dto,
                                             @RequestHeader("X-API-KEY") String apiKey);

    @PutMapping("/api/v1/driver-documents/{id}")
    DriverDocumentRequestDTO updateDocument(@PathVariable("id") Long id,
                                             @RequestBody DriverDocumentRequestDTO dto,
                                             @RequestHeader("X-API-KEY") String apiKey);
}
