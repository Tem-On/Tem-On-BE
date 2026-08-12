package com.example.temon.commerceservice.event.domain.dto;

import com.example.temon.commerceservice.event.domain.entity.EventEntity;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Getter
public class EventResponse {

    private final Long id;
    private final String title;
    private final String description;
    private final String image;
    private final LocalDateTime startAt;
    private final LocalDateTime endAt;
    private final String status;
    private final int productCount;
    private final List<EventProductResponse> products;

    // 기존 코드 호환용
    public EventResponse(EventEntity event) {
        this(event, Collections.emptyList());
    }

    // 상품 포함용
    public EventResponse(EventEntity event, List<EventProductResponse> products) {
        this.id = event.getId();
        this.title = event.getTitle();
        this.description = event.getDescription();
        this.image = "/placeholder.svg";
        this.startAt = event.getStartAt();
        this.endAt = event.getEndAt();
        this.status = event.getStatus().name();
        this.products = products;
        this.productCount = products.size();
    }
}