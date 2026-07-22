package com.example.temon.commerceservice.event.ctrl;

import com.example.temon.commerceservice.event.domain.dto.EventProductResponse;
import com.example.temon.commerceservice.event.domain.dto.EventProductValidationResponse;
import com.example.temon.commerceservice.event.service.EventProductService;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/internal/event-products")
@RequiredArgsConstructor
public class InternalEventProductCtrl {

    private final EventProductService eventProductService;

    @GetMapping
    public List<EventProductResponse> getAllEventProductsInternal() {
        return eventProductService.getAllEventProductsInternal();
    }
    
    @GetMapping("/{eventProductId}")
    public EventProductResponse getEventProduct(
            @PathVariable Long eventProductId
    ) {
        return eventProductService.getEventProductDetail(eventProductId);
    }

    @GetMapping("/{eventProductId}/validation")
    public EventProductValidationResponse validateEventProduct(
            @PathVariable Long eventProductId
    ) {
        return eventProductService.validateForQueue(eventProductId);
    }

    @PostMapping("/batch")
    public List<EventProductResponse> getEventProductsByIds(
            @RequestBody List<Long> eventProductIds
    ) {
        return eventProductService.getEventProductsByIds(eventProductIds); 
    }
}