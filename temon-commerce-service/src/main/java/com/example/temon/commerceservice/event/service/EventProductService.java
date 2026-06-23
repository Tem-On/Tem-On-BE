package com.example.temon.commerceservice.event.service;

import com.example.temon.commerceservice.event.domain.dto.EventProductResponse;
import com.example.temon.commerceservice.event.domain.entity.EventProductEntity;
import com.example.temon.commerceservice.event.repository.EventProductRepository;
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
public class EventProductService {

    private final EventProductRepository eventProductRepository;
    private final ProductRepository productRepository; 

    public List<EventProductResponse> getAllEventProducts() {
        return eventProductRepository.findAllActiveProductsWithoutDeleted().stream()
                .map(ep -> {
                    ProductEntity product = productRepository.findById(ep.getProductId())
                            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 상품입니다. id=" + ep.getProductId()));
                    return new EventProductResponse(ep, product);
                })
                .collect(Collectors.toList());
    }

    public EventProductResponse getEventProductDetail(Long eventProductId) {
        EventProductEntity ep = eventProductRepository.findByIdWithEvent(eventProductId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 이벤트 상품입니다. id=" + eventProductId));
        
        ProductEntity product = productRepository.findById(ep.getProductId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 상품입니다. id=" + ep.getProductId()));
        
        return new EventProductResponse(ep, product);
    }

    public List<EventProductResponse> getProductsByEventId(Long eventId) {
        return eventProductRepository.findByEventId(eventId).stream()
                .map(ep -> {
                    ProductEntity product = productRepository.findById(ep.getProductId())
                            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 상품입니다. id=" + ep.getProductId()));
                    return new EventProductResponse(ep, product);
                })
                .collect(Collectors.toList());
    }
}