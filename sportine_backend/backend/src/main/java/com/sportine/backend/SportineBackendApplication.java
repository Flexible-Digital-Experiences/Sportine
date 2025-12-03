package com.sportine.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import jakarta.annotation.PostConstruct;
import java.util.TimeZone;

@SpringBootApplication
public class SportineBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(SportineBackendApplication.class, args);
    }

    @PostConstruct
    public void init() {
        // Configurar la zona horaria a Ciudad de México
        TimeZone.setDefault(TimeZone.getTimeZone("America/Mexico_City"));
    }

}