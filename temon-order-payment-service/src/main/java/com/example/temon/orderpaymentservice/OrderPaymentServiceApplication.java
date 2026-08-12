package com.example.temon.orderpaymentservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients 
public class OrderPaymentServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrderPaymentServiceApplication.class, args);
    }

}