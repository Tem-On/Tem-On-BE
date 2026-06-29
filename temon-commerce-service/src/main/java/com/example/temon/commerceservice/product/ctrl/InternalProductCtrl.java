package com.example.temon.commerceservice.product.ctrl;

import com.example.temon.commerceservice.product.domain.dto.ProductResponse;
import com.example.temon.commerceservice.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/internal/products")
@RequiredArgsConstructor
public class InternalProductCtrl {

    private final ProductService productService;

    @GetMapping("/{productId}")
    public ProductResponse getProduct(
            @PathVariable Long productId
    ) {
        return productService.findProduct(productId);
    }
}