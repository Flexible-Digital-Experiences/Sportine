package com.sportine.backend.config;

import com.cloudinary.Cloudinary;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class CloudinaryConfig {

    @Bean
    public Cloudinary cloudinary() {
        Map<String, String> config = new HashMap<>();


        config.put("cloud_name", "deyaqiwwd");
        config.put("api_key", "329273734867383");
        config.put("api_secret", "vtDc0qKYxkeD3hFLXpxnkcVaLZg");

        return new Cloudinary(config);
    }
}