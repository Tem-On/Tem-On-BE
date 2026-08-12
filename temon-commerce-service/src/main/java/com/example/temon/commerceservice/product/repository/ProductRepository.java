package com.example.temon.commerceservice.product.repository;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.temon.commerceservice.product.domain.entity.ProductEntity;
import com.example.temon.commerceservice.product.domain.entity.ProductCategory;
import com.example.temon.commerceservice.product.domain.entity.ProductStatus;

public interface ProductRepository extends JpaRepository<ProductEntity, Long> {

    Page<ProductEntity> findByCategoryAndNameContaining(
            ProductCategory category,
            String keyword,
            Pageable pageable
    );

    Page<ProductEntity> findByCategory(
            ProductCategory category,
            Pageable pageable
    );

    Page<ProductEntity> findByNameContaining(
            String keyword,
            Pageable pageable
    );
    
    Page<ProductEntity> findByStatusNot(
            ProductStatus status,
            Pageable pageable
    );
    
}