package com.example.temon.common.dto.event;

import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockChangedEvent {

    private Long eventProductId;

    private int remainingQuantity;

    private int reservedQuantity;

    private int soldQuantity;
}
