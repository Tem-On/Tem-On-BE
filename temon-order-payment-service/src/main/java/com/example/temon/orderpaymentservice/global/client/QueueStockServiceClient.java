package com.example.temon.orderpaymentservice.global.client;

import com.example.temon.orderpaymentservice.global.client.StockRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "temon-queue-stock-service")
public interface QueueStockServiceClient {

    @PostMapping("/api/queue/available")
    Object validatePurchaseAccess(
            @RequestParam("eventProductId") Long eventProductId,
            @RequestParam("userId") Long userId
    );

    @PostMapping("/api/stocks/reserve")
    String reserveStock(@RequestBody StockRequest request);

    @PostMapping("/api/stocks/release")
    String releaseStock(@RequestBody StockRequest request);

    @PostMapping("/api/queue/expire")
    void completeQueue(@RequestParam("eventProductId") Long eventProductId);
}