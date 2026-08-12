package com.example.temon.commerceservice.event.domain.dto;

import com.example.temon.commerceservice.event.domain.entity.EventProductEntity;
import com.example.temon.commerceservice.product.domain.entity.ProductEntity;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class EventProductResponse {

    private final Long id;

    private final Long eventId;
    private final String eventTitle;

    private final Long productId;
    private final String productName;
    private final String productImageUrl;
    private final Integer originalPrice;
    private final String categoryName;
    private final String productStatus;

    private final Integer eventPrice;
    private final Integer purchaseLimit;
    private final String eventProductStatus;
    private final LocalDateTime createdAt;

    private final Integer totalStock;
    private final Integer remainingStock;
    private final Integer reservedStock;
    private final Integer soldCount;

    public EventProductResponse(
            EventProductEntity eventProduct,
            ProductEntity product,
            Integer totalStock,
            Integer remainingStock,
            Integer reservedStock,
            Integer soldCount
    ) {
        this.id = eventProduct.getId();

        this.eventId = eventProduct.getEvent().getId();
        this.eventTitle = eventProduct.getEvent().getTitle();

        this.productId = product.getId();
        this.productName = product.getName();
        this.productImageUrl = product.getImageUrl();
        this.originalPrice = product.getPrice();
        this.categoryName = product.getCategory().getDescription();
        this.productStatus = product.getStatus().name();

        this.eventPrice = eventProduct.getEventPrice();
        this.purchaseLimit = eventProduct.getPurchaseLimit();
        this.eventProductStatus = eventProduct.getStatus().name();
        this.createdAt = eventProduct.getCreatedAt();

        this.totalStock = totalStock != null
                ? totalStock
                : 0;

        this.remainingStock = remainingStock != null
                ? remainingStock
                : 0;

        this.reservedStock = reservedStock != null
                ? reservedStock
                : 0;

        this.soldCount = soldCount != null
                ? soldCount
                : 0;
    }
}