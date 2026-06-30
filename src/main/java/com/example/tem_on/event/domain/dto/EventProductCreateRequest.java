package com.example.tem_on.event.domain.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class EventProductCreateRequest {

    private Long eventId;
    private Long productId;
    private Integer eventPrice;
    private Integer purchaseLimit;
}