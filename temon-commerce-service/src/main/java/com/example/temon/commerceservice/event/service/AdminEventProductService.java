package com.example.temon.commerceservice.event.service;

import com.example.temon.commerceservice.event.domain.dto.EventProductCreateRequest;
import com.example.temon.commerceservice.event.domain.dto.EventProductResponse;
import com.example.temon.commerceservice.event.domain.dto.EventProductStatusUpdateRequest;
import com.example.temon.commerceservice.event.domain.dto.EventProductUpdateRequest;
import com.example.temon.commerceservice.event.domain.dto.StockInfoResponse;
import com.example.temon.commerceservice.event.domain.entity.EventEntity;
import com.example.temon.commerceservice.event.domain.entity.EventProductEntity;
import com.example.temon.commerceservice.event.domain.entity.EventProductStatus;
import com.example.temon.commerceservice.event.repository.EventProductRepository;
import com.example.temon.commerceservice.event.repository.EventRepository;
import com.example.temon.commerceservice.global.client.StockClient;
import com.example.temon.commerceservice.product.domain.entity.ProductEntity;
import com.example.temon.commerceservice.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminEventProductService {

    private final EventProductRepository eventProductRepository;
    private final EventRepository eventRepository;
    private final ProductRepository productRepository;
    private final StockClient stockClient;

    @Transactional
    public EventProductResponse createEventProduct(
            EventProductCreateRequest request
    ) {
        validateCreateRequest(request);

        EventEntity event = eventRepository.findById(
                        request.getEventId()
                )
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "존재하지 않는 이벤트입니다. ID: "
                                        + request.getEventId()
                        )
                );

        ProductEntity product = productRepository.findById(
                        request.getProductId()
                )
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "존재하지 않는 상품입니다. ID: "
                                        + request.getProductId()
                        )
                );

        boolean duplicated =
                eventProductRepository
                        .findByEventId(
                                request.getEventId()
                        )
                        .stream()
                        .anyMatch(eventProduct ->
                                eventProduct.getProductId()
                                        .equals(request.getProductId())
                                        && eventProduct.getStatus()
                                        != EventProductStatus.DELETED
                        );

        if (duplicated) {
            throw new IllegalArgumentException(
                    "이미 해당 이벤트에 등록된 상품입니다."
            );
        }

        EventProductEntity eventProduct =
                EventProductEntity.createEventProduct(
                        event,
                        request.getProductId(),
                        request.getEventPrice(),
                        request.getPurchaseLimit()
                );

        EventProductEntity saved =
                eventProductRepository.saveAndFlush(
                        eventProduct
                );

        /*
         * 이벤트 상품 생성 직후에는 아직 QueueStock에
         * 재고가 등록되지 않았으므로 재고 값은 0으로 응답한다.
         *
         * 프론트는 응답받은 saved.id로
         * POST /api/admin/stocks를 호출한다.
         */
        return createResponseWithStock(
                saved,
                product,
                null
        );
    }

    public List<EventProductResponse> getEventProductList() {
        List<EventProductEntity> eventProducts =
                eventProductRepository.findAllWithEvent()
                        .stream()
                        .filter(eventProduct ->
                                eventProduct.getStatus()
                                        != EventProductStatus.DELETED
                        )
                        .toList();

        if (eventProducts.isEmpty()) {
            return Collections.emptyList();
        }

        List<Long> eventProductIds =
                eventProducts.stream()
                        .map(EventProductEntity::getId)
                        .toList();

        Map<Long, StockInfoResponse> stockMap =
                fetchStockMap(eventProductIds);

        return eventProducts.stream()
                .map(eventProduct -> {
                    ProductEntity product =
                            getProduct(
                                    eventProduct.getProductId()
                            );

                    StockInfoResponse stock =
                            stockMap.get(
                                    eventProduct.getId()
                            );

                    return createResponseWithStock(
                            eventProduct,
                            product,
                            stock
                    );
                })
                .collect(Collectors.toList());
    }

    public EventProductResponse getEventProductDetail(
            Long eventProductId
    ) {
        EventProductEntity eventProduct =
                getActiveEventProduct(eventProductId);

        ProductEntity product =
                getProduct(
                        eventProduct.getProductId()
                );

        Map<Long, StockInfoResponse> stockMap =
                fetchStockMap(
                        List.of(eventProductId)
                );

        return createResponseWithStock(
                eventProduct,
                product,
                stockMap.get(eventProductId)
        );
    }

    @Transactional
    public EventProductResponse updateEventProduct(
            Long eventProductId,
            EventProductUpdateRequest request
    ) {
        validateUpdateRequest(request);

        EventProductEntity eventProduct =
                getActiveEventProduct(eventProductId);

        ProductEntity product =
                getProduct(request.getProductId());

        eventProduct.updateProductInfo(
                request.getProductId(),
                request.getEventPrice(),
                request.getPurchaseLimit()
        );

        eventProductRepository.flush();

        Map<Long, StockInfoResponse> stockMap =
                fetchStockMap(
                        List.of(eventProductId)
                );

        return createResponseWithStock(
                eventProduct,
                product,
                stockMap.get(eventProductId)
        );
    }

    @Transactional
    public EventProductResponse updateEventProductStatus(
            Long eventProductId,
            EventProductStatusUpdateRequest request
    ) {
        EventProductEntity eventProduct =
                getActiveEventProduct(eventProductId);

        if (
                request.getStatus() == null ||
                request.getStatus().isBlank()
        ) {
            throw new IllegalArgumentException(
                    "변경할 상태를 입력해주세요."
            );
        }

        EventProductStatus newStatus;

        try {
            newStatus = EventProductStatus.valueOf(
                    request.getStatus()
                            .trim()
                            .toUpperCase()
            );
        } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException(
                    "지원하지 않는 이벤트 상품 상태입니다: "
                            + request.getStatus()
            );
        }

        if (newStatus == EventProductStatus.DELETED) {
            throw new IllegalArgumentException(
                    "삭제 상태 변경은 삭제 API를 사용해주세요."
            );
        }

        eventProduct.updateStatus(newStatus);
        eventProductRepository.flush();

        ProductEntity product =
                getProduct(
                        eventProduct.getProductId()
                );

        Map<Long, StockInfoResponse> stockMap =
                fetchStockMap(
                        List.of(eventProductId)
                );

        return createResponseWithStock(
                eventProduct,
                product,
                stockMap.get(eventProductId)
        );
    }

    @Transactional
    public void deleteEventProduct(
            Long eventProductId
    ) {
        EventProductEntity eventProduct =
                eventProductRepository.findById(
                                eventProductId
                        )
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "존재하지 않는 이벤트 상품입니다. ID: "
                                                + eventProductId
                                )
                        );

        if (
                eventProduct.getStatus()
                        == EventProductStatus.DELETED
        ) {
            throw new IllegalArgumentException(
                    "이미 삭제된 이벤트 상품입니다."
            );
        }

        eventProduct.delete();
    }

    private EventProductEntity getActiveEventProduct(
            Long eventProductId
    ) {
        EventProductEntity eventProduct =
                eventProductRepository.findByIdWithEvent(
                                eventProductId
                        )
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "존재하지 않는 이벤트 상품입니다. ID: "
                                                + eventProductId
                                )
                        );

        if (
                eventProduct.getStatus()
                        == EventProductStatus.DELETED
        ) {
            throw new IllegalArgumentException(
                    "삭제된 이벤트 상품입니다. ID: "
                            + eventProductId
            );
        }

        return eventProduct;
    }

    private ProductEntity getProduct(
            Long productId
    ) {
        return productRepository.findById(productId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "상품 정보가 존재하지 않습니다. ID: "
                                        + productId
                        )
                );
    }

    private void validateCreateRequest(
            EventProductCreateRequest request
    ) {
        if (request == null) {
            throw new IllegalArgumentException(
                    "요청 정보가 존재하지 않습니다."
            );
        }

        if (request.getEventId() == null) {
            throw new IllegalArgumentException(
                    "이벤트 ID를 입력해주세요."
            );
        }

        if (request.getProductId() == null) {
            throw new IllegalArgumentException(
                    "상품 ID를 입력해주세요."
            );
        }

        validatePriceAndLimit(
                request.getEventPrice(),
                request.getPurchaseLimit()
        );
    }

    private void validateUpdateRequest(
            EventProductUpdateRequest request
    ) {
        if (request == null) {
            throw new IllegalArgumentException(
                    "요청 정보가 존재하지 않습니다."
            );
        }

        if (request.getProductId() == null) {
            throw new IllegalArgumentException(
                    "상품 ID를 입력해주세요."
            );
        }

        validatePriceAndLimit(
                request.getEventPrice(),
                request.getPurchaseLimit()
        );
    }

    private void validatePriceAndLimit(
            Integer eventPrice,
            Integer purchaseLimit
    ) {
        if (
                eventPrice == null ||
                eventPrice <= 0
        ) {
            throw new IllegalArgumentException(
                    "이벤트 가격은 1원 이상이어야 합니다."
            );
        }

        if (
                purchaseLimit != null &&
                purchaseLimit <= 0
        ) {
            throw new IllegalArgumentException(
                    "구매 제한은 1개 이상이어야 합니다."
            );
        }
    }

    private Map<Long, StockInfoResponse> fetchStockMap(
            List<Long> eventProductIds
    ) {
        if (
                eventProductIds == null ||
                eventProductIds.isEmpty()
        ) {
            return Collections.emptyMap();
        }

        try {
            List<StockInfoResponse> stocks =
                    stockClient.getStocksByProductIds(
                            eventProductIds
                    );

            if (stocks == null) {
                return Collections.emptyMap();
            }

            return stocks.stream()
                    .collect(
                            Collectors.toMap(
                                    StockInfoResponse::getEventProductId,
                                    stock -> stock,
                                    (first, second) -> first
                            )
                    );
        } catch (Exception exception) {
            log.error(
                    "QueueStockService 재고 조회 실패. eventProductIds={}, reason={}",
                    eventProductIds,
                    exception.getMessage()
            );

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
}