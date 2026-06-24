package com.example.temon.queuestockservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class QueueStockServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(
                QueueStockServiceApplication.class,
                args
        );
    }
}