package com.example.temon.commerceservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class TemonCommerceServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(TemonCommerceServiceApplication.class, args);
    }
}