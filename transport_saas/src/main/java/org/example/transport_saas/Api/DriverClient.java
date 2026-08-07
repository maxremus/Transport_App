package org.example.transport_saas.Api;

import java.util.List;

import org.example.transport_saas.DTO.DriverCreateDTO;
import org.example.transport_saas.DTO.DriverDTO;
import org.example.transport_saas.DTO.DriverDocumentRequestDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
        name = "driver-service",
        url = "${driver.service.url}"
)
public interface DriverClient {

    @PostMapping("/api/v1/driver-documents")
    void save(@RequestBody DriverDocumentRequestDTO dto);

    @GetMapping("/api/v1/drivers")
    List<DriverDTO> getAllDrivers();

    @PostMapping("/api/v1/drivers")
    void createDriver(@RequestBody DriverCreateDTO dto);
}
