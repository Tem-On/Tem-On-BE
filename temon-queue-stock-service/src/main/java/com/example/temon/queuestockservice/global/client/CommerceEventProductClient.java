package com.example.temon.queuestockservice.global.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(
        name = "commerce-service",
        url = "${commerce-service.url}"
)
public interface CommerceEventProductClient {

    @GetMapping("/internal/event-products/{eventProductId}/validation")
    EventProductValidationResponse validateEventProduct(
            @PathVariable Long eventProductId
    );
}