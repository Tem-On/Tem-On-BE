package com.example.temon.commerceservice.event.ctrl;

import com.example.temon.commerceservice.event.domain.dto.EventProductValidationResponse;
import com.example.temon.commerceservice.event.service.EventProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/internal/event-products")
@RequiredArgsConstructor
public class InternalEventProductCtrl {

    private final EventProductService eventProductService;

    @GetMapping("/{eventProductId}/validation")
    public EventProductValidationResponse validateEventProduct(
            @PathVariable Long eventProductId
    ) {
        return eventProductService.validateForQueue(eventProductId);
    }
}