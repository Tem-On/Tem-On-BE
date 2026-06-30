package com.example.tem_on.product.service;

import com.example.tem_on.product.domain.dto.AdminProductResponse;
import com.example.tem_on.product.domain.dto.ProductCreateRequest;
import com.example.tem_on.product.domain.dto.ProductUpdateRequest;
import com.example.tem_on.product.domain.entity.Product;
import com.example.tem_on.product.domain.entity.ProductStatus;
import com.example.tem_on.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminProductService {

    private final ProductRepository productRepository;

    @Transactional
    public AdminProductResponse createProduct(ProductCreateRequest request) {
        Product product = Product.builder()
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .imageUrl(request.getImageUrl())
                .category(request.getCategory())
                .status(ProductStatus.ACTIVE)
                .build();

        Product savedProduct = productRepository.save(product);

        return AdminProductResponse.from(savedProduct);
    }

    @Transactional(readOnly = true)
    public Page<AdminProductResponse> getProducts(Pageable pageable) {
        return productRepository.findByStatusNot(ProductStatus.DELETED, pageable)
                .map(AdminProductResponse::from);
    }

    @Transactional(readOnly = true)
    public AdminProductResponse getProduct(Long productId) {
        Product product = getProductEntity(productId);

        return AdminProductResponse.from(product);
    }

    @Transactional
    public AdminProductResponse updateProduct(
            Long productId,
            ProductUpdateRequest request
    ) {
        Product product = getProductEntity(productId);

        product.update(
                request.getName(),
                request.getDescription(),
                request.getPrice(),
                request.getImageUrl(),
                request.getCategory(),
                request.getStatus()
        );

        return AdminProductResponse.from(product);
    }

    @Transactional
    public void deleteProduct(Long productId) {
        Product product = getProductEntity(productId);

        product.delete();
    }

    private Product getProductEntity(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다."));
    }
}