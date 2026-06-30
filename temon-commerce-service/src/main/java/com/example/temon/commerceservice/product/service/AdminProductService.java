package com.example.temon.commerceservice.product.service;


import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.temon.commerceservice.product.domain.dto.AdminProductResponse;
import com.example.temon.commerceservice.product.domain.dto.ProductCreateRequest;
import com.example.temon.commerceservice.product.domain.dto.ProductUpdateRequest;
import com.example.temon.commerceservice.product.domain.entity.ProductEntity;
import com.example.temon.commerceservice.product.domain.entity.ProductStatus;
import com.example.temon.commerceservice.product.repository.ProductRepository;

@Service
@RequiredArgsConstructor
public class AdminProductService {

    private final ProductRepository productRepository;

    @Transactional
    public AdminProductResponse createProduct(ProductCreateRequest request) {
        ProductEntity product = ProductEntity.builder()
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .imageUrl(request.getImageUrl())
                .category(request.getCategory())
                .status(ProductStatus.ACTIVE)
                .build();

        ProductEntity savedProduct = productRepository.save(product);

        return AdminProductResponse.from(savedProduct);
    }

    @Transactional(readOnly = true)
    public Page<AdminProductResponse> getProducts(Pageable pageable) {
        return productRepository.findByStatusNot(ProductStatus.DELETED, pageable)
                .map(AdminProductResponse::from);
    }

    @Transactional(readOnly = true)
    public AdminProductResponse getProduct(Long productId) {
        ProductEntity product = getProductEntity(productId);

        return AdminProductResponse.from(product);
    }

    @Transactional
    public AdminProductResponse updateProduct(
            Long productId,
            ProductUpdateRequest request
    ) {
        ProductEntity product = getProductEntity(productId);

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
        ProductEntity product = getProductEntity(productId);

        product.delete();
    }

    private ProductEntity getProductEntity(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다."));
    }
}