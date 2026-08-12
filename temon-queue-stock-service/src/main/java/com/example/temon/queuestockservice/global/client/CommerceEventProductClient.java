package com.example.temon.queuestockservice.global.client;

import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(
        name = "commerce-service",
        url = "${commerce-service.url}"
)
public interface CommerceEventProductClient {

    @GetMapping("/internal/event-products")
    List<EventProductClientResponse> getAllEventProducts();

    @GetMapping("/internal/event-products/{eventProductId}")
    EventProductClientResponse getEventProduct(
            @PathVariable("eventProductId") Long eventProductId
    );

    @GetMapping("/internal/event-products/{eventProductId}/validation")
    EventProductValidationResponse validateEventProduct(
            @PathVariable("eventProductId") Long eventProductId
    );
}