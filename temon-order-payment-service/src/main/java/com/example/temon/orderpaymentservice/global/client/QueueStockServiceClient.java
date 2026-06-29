package com.example.temon.orderpaymentservice.global.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(
        name = "temon-queue-stock-service",
        url = "${service.queue-stock.url}"
)
public interface QueueStockServiceClient {

    @GetMapping("/internal/queue/available")
    Object validatePurchaseAccess(
            @RequestParam("eventProductId") Long eventProductId,
            @RequestParam("userId") Long userId
    );

    @PostMapping("/internal/stocks/reserve")
    String reserveStock(@RequestBody StockRequest request);

    @PostMapping("/internal/stocks/release")
    String releaseStock(@RequestBody StockRequest request);

    @PostMapping("/internal/queue/expire")
    void completeQueue(@RequestParam("eventProductId") Long eventProductId);

    @GetMapping("/api/admin/stocks")
    List<StockResponse> getStockList();
}