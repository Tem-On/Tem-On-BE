package com.example.temon.commerceservice.global.client;

import com.example.temon.commerceservice.event.domain.dto.StockInfoResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(
        name = "temon-queue-stock-service",
        url = "${QUEUE_STOCK_URL:http://localhost:8084}"
)
public interface StockClient {

    @GetMapping("/internal/stocks/bulk")
    List<StockInfoResponse> getStocksByProductIds(
            @RequestParam("eventProductIds")
            List<Long> eventProductIds
    );
}