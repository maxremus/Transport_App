package org.example.transport_saas;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class TransportSaasApplication {

    public static void main(String[] args) {
        SpringApplication.run(TransportSaasApplication.class, args);
    }

}
