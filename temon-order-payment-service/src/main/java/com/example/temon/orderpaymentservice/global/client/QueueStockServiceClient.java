package com.example.temon.orderpaymentservice.global.client;

import com.example.temon.orderpaymentservice.global.client.StockRequest;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

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

    @GetMapping("/api/admin/stocks")
    List<StockResponse> getStockList();
}