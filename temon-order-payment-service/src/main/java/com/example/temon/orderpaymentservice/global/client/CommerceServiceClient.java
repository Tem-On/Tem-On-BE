package com.example.temon.orderpaymentservice.global.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(
        name = "temon-commerce-service",
        url = "${service.commerce.url}"
)
public interface CommerceServiceClient {

    @GetMapping("/internal/event-products/{id}")
    EventProductResponse getEventProduct(@PathVariable("id") Long id);

    @PostMapping("/internal/event-products/batch")
    List<EventProductResponse> getEventProductsByIds(@RequestBody List<Long> eventProductIds);
    
    @GetMapping("/internal/products/{id}")
    ProductResponse getProduct(@PathVariable("id") Long id);

    @GetMapping("/api/events/open")
    List<EventResponse> getOpenEvents();

    @GetMapping("/api/admin/events/{eventId}")
    EventResponse getEventDetail(@PathVariable("eventId") Long eventId);

    @GetMapping("/api/events/{eventId}/products")
    List<EventProductResponse> getProductsByEventId(@PathVariable("eventId") Long eventId);
    
    @PostMapping("/internal/products/batch")
    List<ProductResponse> getProductsByIds(@RequestBody List<Long> ids);
}