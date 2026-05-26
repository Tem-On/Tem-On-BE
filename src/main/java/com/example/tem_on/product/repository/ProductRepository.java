package com.example.tem_on.product.repository;

import com.example.tem_on.product.domain.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {

    Page<Product> findByCategoryAndNameContaining(
            String category,
            String keyword,
            Pageable pageable
    );

    Page<Product> findByCategory(
            String category,
            Pageable pageable
    );

    Page<Product> findByNameContaining(
            String keyword,
            Pageable pageable
    );
}