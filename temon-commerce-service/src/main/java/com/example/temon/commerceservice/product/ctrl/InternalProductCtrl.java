package com.example.temon.commerceservice.product.ctrl;

import com.example.temon.commerceservice.product.domain.dto.ProductResponse;
import com.example.temon.commerceservice.product.service.ProductService;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/internal/products")
@RequiredArgsConstructor
public class InternalProductCtrl {

    private final ProductService productService;

    @GetMapping("/{productId}")
    public ProductResponse getProduct(
            @PathVariable Long productId) {
        return productService.findProduct(productId);
    }

    @PostMapping("/batch")
    public ResponseEntity<List<ProductResponse>> getProductsByIds(@RequestBody List<Long> ids) {
        return ResponseEntity.ok(productService.getProductsByIds(ids));
    }
}