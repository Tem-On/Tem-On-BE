package com.example.temon.queuestockservice.stock.service;

import com.example.temon.queuestockservice.global.client.CommerceEventProductClient;
import com.example.temon.queuestockservice.global.client.EventProductClientResponse;
import com.example.temon.queuestockservice.stock.domain.dto.StockRequest;
import com.example.temon.queuestockservice.stock.domain.dto.StockResponse;
import com.example.temon.queuestockservice.stock.domain.entity.StockEntity;
import com.example.temon.queuestockservice.stock.repository.StockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminStockService {

    private final StockRepository stockRepository;
    private final CommerceEventProductClient commerceEventProductClient;

    @Transactional
    public void createStock(StockRequest request) {
        if (request == null) {
            throw new IllegalArgumentException(
                    "재고 등록 요청이 존재하지 않습니다."
            );
        }

        if (request.getEventProductId() == null) {
            throw new IllegalArgumentException(
                    "이벤트 상품 ID를 입력해주세요."
            );
        }

        if (request.getQuantity() <= 0) {
            throw new IllegalArgumentException(
                    "재고 수량은 1개 이상이어야 합니다."
            );
        }

        stockRepository
                .findByEventProductId(
                        request.getEventProductId()
                )
                .ifPresent(stock -> {
                    throw new IllegalArgumentException(
                            "이미 재고가 등록된 이벤트 상품입니다. ID: "
                                    + request.getEventProductId()
                    );
                });

        StockEntity stock = StockEntity.builder()
                .eventProductId(
                        request.getEventProductId()
                )
                .totalQuantity(
                        request.getQuantity()
                )
                .remainingQuantity(
                        request.getQuantity()
                )
                .reservedQuantity(0)
                .soldQuantity(0)
                .build();

        stockRepository.save(stock);
    }

    public List<StockResponse> getStockList() {
        Map<Long, EventProductClientResponse> eventProductMap;

        try {
            List<EventProductClientResponse> eventProducts =
                    commerceEventProductClient
                            .getAllEventProducts();

            eventProductMap = eventProducts.stream()
                    .collect(
                            Collectors.toMap(
                                    EventProductClientResponse::id,
                                    Function.identity(),
                                    (first, second) -> first
                            )
                    );
        } catch (Exception exception) {
            eventProductMap = Map.of();
        }

        List<StockEntity> stocks =
                stockRepository.findAll();

        Map<Long, EventProductClientResponse> finalMap =
                eventProductMap;

        return stocks.stream()
                .map(stock -> {
                    EventProductClientResponse productInfo =
                            finalMap.get(
                                    stock.getEventProductId()
                            );

                    return new StockResponse(
                            stock,
                            productInfo
                    );
                })
                .collect(Collectors.toList());
    }

    public StockResponse getStockDetail(
            Long eventProductId
    ) {
        StockEntity stock =
                stockRepository
                        .findByEventProductId(
                                eventProductId
                        )
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "재고 정보가 존재하지 않습니다. 상품 ID: "
                                                + eventProductId
                                )
                        );

        EventProductClientResponse productInfo = null;

        try {
            productInfo =
                    commerceEventProductClient
                            .getEventProduct(
                                    eventProductId
                            );
        } catch (Exception ignored) {
        }

        return new StockResponse(
                stock,
                productInfo
        );
    }

    /**
     * 재고가 있으면 수정한다.
     * 기존 이벤트 상품처럼 재고 데이터가 없으면 새로 생성한다.
     */
    @Transactional
    public void updateStockQuantity(
            Long eventProductId,
            int newQuantity
    ) {
        if (eventProductId == null) {
            throw new IllegalArgumentException(
                    "이벤트 상품 ID를 입력해주세요."
            );
        }

        if (newQuantity <= 0) {
            throw new IllegalArgumentException(
                    "재고 수량은 1개 이상이어야 합니다."
            );
        }

        StockEntity stock =
                stockRepository
                        .findByEventProductId(
                                eventProductId
                        )
                        .orElseGet(() -> {
                            StockEntity newStock =
                                    StockEntity.builder()
                                            .eventProductId(
                                                    eventProductId
                                            )
                                            .totalQuantity(
                                                    newQuantity
                                            )
                                            .remainingQuantity(
                                                    newQuantity
                                            )
                                            .reservedQuantity(0)
                                            .soldQuantity(0)
                                            .build();

                            return stockRepository.save(
                                    newStock
                            );
                        });

        /*
         * 새로 생성된 재고도 같은 수량이므로 문제없이 처리된다.
         *
         * 기존 재고라면 판매/선점 수량을 유지하면서
         * totalQuantity와 remainingQuantity가 재계산된다.
         */
        stock.updateQuantity(newQuantity);
    }

    @Transactional
    public void forceSoldOut(
            Long eventProductId
    ) {
        StockEntity stock =
                stockRepository
                        .findByEventProductId(
                                eventProductId
                        )
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "재고 정보가 존재하지 않습니다. 상품 ID: "
                                                + eventProductId
                                )
                        );

        stock.forceSoldOut();
    }
}