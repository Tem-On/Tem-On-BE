package com.example.temon.commerceservice.event.domain.dto;


import lombok.Getter;
import java.time.LocalDateTime;
import com.example.temon.commerceservice.event.domain.entity.EventProductEntity;
import com.example.temon.commerceservice.product.domain.entity.ProductEntity;

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
    private final Integer soldCount;

    public EventProductResponse(EventProductEntity epEntity, ProductEntity product, Integer totalStock, Integer remainingStock, Integer soldCount) {
        this.id = epEntity.getId();
        this.eventId = epEntity.getEvent().getId();
        this.eventTitle = epEntity.getEvent().getTitle();
        
        this.productId = product.getId();
        this.productName = product.getName();
        this.productImageUrl = product.getImageUrl();
        this.originalPrice = product.getPrice();
        this.categoryName = product.getCategory().getDescription(); 
        this.productStatus = product.getStatus().name();
        
        this.eventPrice = epEntity.getEventPrice();
        this.purchaseLimit = epEntity.getPurchaseLimit();
        this.eventProductStatus = epEntity.getStatus().name();
        this.createdAt = epEntity.getCreatedAt();

        this.totalStock = totalStock != null ? totalStock : 0;
        this.remainingStock = remainingStock != null ? remainingStock : 0;
        this.soldCount = soldCount != null ? soldCount : 0;
    }
}