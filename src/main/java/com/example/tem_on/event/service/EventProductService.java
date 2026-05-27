package com.example.tem_on.event.service;

import com.example.tem_on.event.domain.dto.EventProductResponse;
import com.example.tem_on.event.domain.entity.EventProductEntity;
import com.example.tem_on.event.repository.EventProductRepository;
import com.example.tem_on.product.domain.entity.Product;
import com.example.tem_on.product.repository.ProductRepository;

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
        return eventProductRepository.findAllWithEvent().stream()
                .map(ep -> {
                    Product product = productRepository.findById(ep.getProductId())
                            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 상품입니다. id=" + ep.getProductId()));
                    return new EventProductResponse(ep, product);
                })
                .collect(Collectors.toList());
    }

    public EventProductResponse getEventProductDetail(Long eventProductId) {
        EventProductEntity ep = eventProductRepository.findByIdWithEvent(eventProductId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 이벤트 상품입니다. id=" + eventProductId));
        
        Product product = productRepository.findById(ep.getProductId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 상품입니다. id=" + ep.getProductId()));
        
        return new EventProductResponse(ep, product);
    }

    public List<EventProductResponse> getProductsByEventId(Long eventId) {
        return eventProductRepository.findByEventId(eventId).stream()
                .map(ep -> {
                    Product product = productRepository.findById(ep.getProductId())
                            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 상품입니다. id=" + ep.getProductId()));
                    return new EventProductResponse(ep, product);
                })
                .collect(Collectors.toList());
    }
}