package com.example.temon.commerceservice.event.service;

import com.example.temon.commerceservice.event.domain.dto.EventProductCreateRequest;
import com.example.temon.commerceservice.event.domain.dto.EventProductResponse;
import com.example.temon.commerceservice.event.domain.dto.EventProductStatusUpdateRequest;
import com.example.temon.commerceservice.event.domain.dto.EventProductUpdateRequest;
import com.example.temon.commerceservice.event.domain.entity.EventEntity;
import com.example.temon.commerceservice.event.domain.entity.EventProductEntity;
import com.example.temon.commerceservice.event.domain.entity.EventProductStatus;
import com.example.temon.commerceservice.event.repository.EventProductRepository;
import com.example.temon.commerceservice.event.repository.EventRepository;
import com.example.temon.commerceservice.product.domain.entity.ProductEntity;
import com.example.temon.commerceservice.product.repository.ProductRepository; 
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminEventProductService {

    private final EventProductRepository eventProductRepository;
    private final EventRepository eventRepository;
    private final ProductRepository productRepository; 

    @Transactional
    public void createEventProduct(EventProductCreateRequest request) {
        EventEntity event = eventRepository.findById(request.getEventId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 이벤트입니다. ID: " + request.getEventId()));

        if (!productRepository.existsById(request.getProductId())) {
            throw new IllegalArgumentException("존재하지 않는 상품입니다. ID: " + request.getProductId());
        }

        EventProductEntity product = EventProductEntity.createEventProduct(
                event, 
                request.getProductId(), 
                request.getEventPrice(), 
                request.getPurchaseLimit()
        );

        eventProductRepository.save(product);
    }

    public List<EventProductResponse> getEventProductList() {
        return eventProductRepository.findAllWithEvent().stream()
                .map(productEntity -> {
                    ProductEntity product = productRepository.findById(productEntity.getProductId())
                            .orElseThrow(() -> new IllegalArgumentException("상품 정보가 존재하지 않습니다. ID: " + productEntity.getProductId()));
                    return new EventProductResponse(productEntity, product);
                })
                .collect(Collectors.toList());
    }

    public EventProductResponse getEventProductDetail(Long eventProductId) {
        EventProductEntity productEntity = eventProductRepository.findById(eventProductId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 이벤트 상품입니다. ID: " + eventProductId));

        if (productEntity.getStatus() == EventProductStatus.DELETED) {
            throw new IllegalArgumentException("이미 삭제 처리된 이벤트 상품입니다. ID: " + eventProductId);
        }

        ProductEntity product = productRepository.findById(productEntity.getProductId())
                .orElseThrow(() -> new IllegalArgumentException("상품 정보가 존재하지 않습니다. ID: " + productEntity.getProductId()));

        return new EventProductResponse(productEntity, product);
    }

    @Transactional
    public void updateEventProduct(Long eventProductId, EventProductUpdateRequest request) {
        EventProductEntity product = eventProductRepository.findById(eventProductId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 이벤트 상품입니다. ID: " + eventProductId));

        if (product.getStatus() == EventProductStatus.DELETED) {
            throw new IllegalArgumentException("삭제된 상품은 수정할 수 없습니다.");
        }

        product.updateProductInfo(request.getProductId(), request.getEventPrice(), request.getPurchaseLimit());
    }

    @Transactional
    public void updateEventProductStatus(Long eventProductId, EventProductStatusUpdateRequest request) {
        EventProductEntity product = eventProductRepository.findById(eventProductId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 이벤트 상품입니다. ID: " + eventProductId));

        if (product.getStatus() == EventProductStatus.DELETED) {
            throw new IllegalArgumentException("삭제된 상품의 상태는 변경할 수 없습니다.");
        }

        EventProductStatus newStatus = EventProductStatus.valueOf(request.getStatus().toUpperCase());
        product.updateStatus(newStatus);
    }

    @Transactional
    public void deleteEventProduct(Long eventProductId) {
        EventProductEntity product = eventProductRepository.findById(eventProductId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 이벤트 상품입니다. ID: " + eventProductId));

        product.delete();
    }
}