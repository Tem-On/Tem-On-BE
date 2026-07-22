package com.example.temon.commerceservice.event.service;

import com.example.temon.commerceservice.event.domain.dto.EventProductResponse;
import com.example.temon.commerceservice.event.domain.dto.EventProductValidationResponse;
import com.example.temon.commerceservice.event.domain.dto.StockInfoResponse; 
import com.example.temon.commerceservice.event.domain.entity.EventProductEntity;
import com.example.temon.commerceservice.event.domain.entity.EventProductStatus;
import com.example.temon.commerceservice.event.domain.entity.EventStatus;
import com.example.temon.commerceservice.event.repository.EventProductRepository;
import com.example.temon.commerceservice.global.client.StockClient;
import com.example.temon.commerceservice.product.domain.entity.ProductEntity;
import com.example.temon.commerceservice.product.repository.ProductRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EventProductService {

    private final EventProductRepository eventProductRepository;
    private final ProductRepository productRepository; 
    private final StockClient stockClient; 

    public List<EventProductResponse> getAllEventProducts() {
        List<EventProductEntity> epEntities = eventProductRepository.findAllActiveProductsWithoutDeleted(EventProductStatus.DELETED);
        
        Map<Long, StockInfoResponse> stockMap = fetchStockMap(epEntities.stream().map(EventProductEntity::getId).toList());

        return epEntities.stream()
                .map(ep -> {
                    ProductEntity product = productRepository.findById(ep.getProductId())
                            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 상품입니다. id=" + ep.getProductId()));
                    
                    StockInfoResponse stock = stockMap.get(ep.getId());
                    return createResponseWithStock(ep, product, stock);
                })
                .collect(Collectors.toList());
    }

    public EventProductResponse getEventProductDetail(Long eventProductId) {
        EventProductEntity ep = eventProductRepository.findByIdWithEvent(eventProductId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 이벤트 상품입니다. id=" + eventProductId));
        
        ProductEntity product = productRepository.findById(ep.getProductId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 상품입니다. id=" + ep.getProductId()));
        
        Map<Long, StockInfoResponse> stockMap = fetchStockMap(List.of(eventProductId));
        StockInfoResponse stock = stockMap.get(eventProductId);

        return createResponseWithStock(ep, product, stock);
    }

    public List<EventProductResponse> getProductsByEventId(Long eventId) {
        List<EventProductEntity> epEntities = eventProductRepository.findByEventId(eventId);
        Map<Long, StockInfoResponse> stockMap = fetchStockMap(epEntities.stream().map(EventProductEntity::getId).toList());

        return epEntities.stream()
                .map(ep -> {
                    ProductEntity product = productRepository.findById(ep.getProductId())
                            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 상품입니다. id=" + ep.getProductId()));
                    
                    StockInfoResponse stock = stockMap.get(ep.getId());
                    return createResponseWithStock(ep, product, stock);
                })
                .collect(Collectors.toList());
    }

    public EventProductValidationResponse validateForQueue(Long eventProductId) {
        return eventProductRepository.findByIdWithEvent(eventProductId)
                .map(eventProduct -> {
                    boolean queueAvailable =
                            eventProduct.getEvent().getStatus() == EventStatus.OPEN
                                    && eventProduct.getStatus() == EventProductStatus.ON_SALE;

                    return new EventProductValidationResponse(
                            eventProduct.getId(),
                            true,
                            queueAvailable,
                            eventProduct.getEvent().getStatus().name(),
                            eventProduct.getStatus().name()
                    );
                })
                .orElseGet(() -> new EventProductValidationResponse(
                        eventProductId,
                        false,
                        false,
                        null,
                        null
                ));
    }

    public List<EventProductResponse> getPopularProducts() {
        List<EventProductEntity> epEntities = eventProductRepository.findAllActiveProductsWithoutDeleted(EventProductStatus.DELETED).stream()
                .limit(4)
                .toList();
        
        Map<Long, StockInfoResponse> stockMap = fetchStockMap(epEntities.stream().map(EventProductEntity::getId).toList());

        return epEntities.stream()
                .map(ep -> {
                    ProductEntity product = productRepository.findById(ep.getProductId())
                            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 상품입니다. id=" + ep.getProductId()));
                    
                    StockInfoResponse stock = stockMap.get(ep.getId());
                    return createResponseWithStock(ep, product, stock);
                })
                .collect(Collectors.toList());
    }

    public List<EventProductResponse> getShowcaseProducts() {
        List<EventProductEntity> epEntities = eventProductRepository.findAllActiveProductsWithoutDeleted(EventProductStatus.DELETED).stream()
                .filter(ep -> ep.getStatus() == EventProductStatus.ON_SALE || ep.getStatus() == EventProductStatus.READY)
                .toList();

        Map<Long, StockInfoResponse> stockMap = fetchStockMap(epEntities.stream().map(EventProductEntity::getId).toList());

        return epEntities.stream()
                .map(ep -> {
                    ProductEntity product = productRepository.findById(ep.getProductId())
                            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 상품입니다. id=" + ep.getProductId()));
                    
                    StockInfoResponse stock = stockMap.get(ep.getId());
                    return createResponseWithStock(ep, product, stock);
                })
                .collect(Collectors.toList());
    }


    private Map<Long, StockInfoResponse> fetchStockMap(List<Long> eventProductIds) {
        if (eventProductIds == null || eventProductIds.isEmpty()) {
            return Collections.emptyMap();
        }
        try {
            List<StockInfoResponse> stocks = stockClient.getStocksByProductIds(eventProductIds);
            return stocks.stream()
                    .collect(Collectors.toMap(StockInfoResponse::getEventProductId, s -> s, (a, b) -> a));
        } catch (Exception e) {

            return Collections.emptyMap();
        }
    }


    private EventProductResponse createResponseWithStock(
            EventProductEntity eventProduct,
            ProductEntity product,
            StockInfoResponse stock
    ) {
        return new EventProductResponse(
                eventProduct,
                product,
                stock != null
                        ? stock.getTotalQuantity()
                        : 0,
                stock != null
                        ? stock.getRemainingQuantity()
                        : 0,
                stock != null
                        ? stock.getReservedQuantity()
                        : 0,
                stock != null
                        ? stock.getSoldQuantity()
                        : 0
        );
    }

    public List<EventProductResponse> getAllEventProductsInternal() {
        
        List<EventProductEntity> epEntities = eventProductRepository.findAllWithEvent();

        if (epEntities.isEmpty()) {
            return Collections.emptyList();
        }

        List<Long> epIds = epEntities.stream().map(EventProductEntity::getId).toList();
        Map<Long, StockInfoResponse> stockMap = fetchStockMap(epIds);

        List<EventProductResponse> result = new ArrayList<>();
        for (EventProductEntity ep : epEntities) {
            ProductEntity product = productRepository.findById(ep.getProductId()).orElse(null);
            if (product != null) {
                StockInfoResponse stock = stockMap.get(ep.getId());
                result.add(createResponseWithStock(ep, product, stock));
            } else {

            }
        }

        return result;
    }

    public List<EventProductResponse> getEventProductsByIds(List<Long> eventProductIds) {
    if (eventProductIds == null || eventProductIds.isEmpty()) {
        return Collections.emptyList();
    }

    List<EventProductEntity> epEntities = eventProductRepository.findAllById(eventProductIds);
    if (epEntities.isEmpty()) {
        return Collections.emptyList();
    }

    Map<Long, StockInfoResponse> stockMap = fetchStockMap(eventProductIds);

    List<EventProductResponse> result = new ArrayList<>();
        for (EventProductEntity ep : epEntities) {
                ProductEntity product = productRepository.findById(ep.getProductId()).orElse(null);
                if (product != null) {
                StockInfoResponse stock = stockMap.get(ep.getId());
                result.add(createResponseWithStock(ep, product, stock));
                } else {

                }
        }

        return result;
        }
}