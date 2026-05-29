package com.example.tem_on.stock.domain.entity;

import java.time.LocalDateTime;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "stocks")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED) 
@AllArgsConstructor
@Builder 
@EntityListeners(AuditingEntityListener.class) 
public class StockEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; 

    @Column(name = "event_product_id", nullable = false, unique = true)
    private Long eventProductId;

    @Column(nullable = false)
    private int totalQuantity;

    @Column(nullable = false)
    private int remainingQuantity;

    @Column(nullable = false)
    private int reservedQuantity;

    @Column(nullable = false)
    private int soldQuantity; 

    @Version 
    private Long version;

    @LastModifiedDate 
    @Column(nullable = false)
    private LocalDateTime updatedAt; 


    public void reserve(int quantity) {
        if (this.remainingQuantity < quantity) {
            throw new IllegalArgumentException("남은 재고가 부족하여 선점할 수 없습니다.");
        }
        this.remainingQuantity -= quantity;
        this.reservedQuantity += quantity;
    }

    public void decrease(int quantity) {
        if (this.reservedQuantity < quantity) {
            throw new IllegalArgumentException("차감할 임시 선점 재고가 부족합니다.");
        }
        this.reservedQuantity -= quantity;
        this.soldQuantity += quantity;
    }

    public void restore(int quantity) {
        if (this.reservedQuantity < quantity) {
            throw new IllegalArgumentException("복구할 임시 선점 재고가 부족합니다.");
        }
        this.reservedQuantity -= quantity;
        this.remainingQuantity += quantity;
    }
}