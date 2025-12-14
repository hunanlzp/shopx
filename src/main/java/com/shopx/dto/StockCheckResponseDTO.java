package com.shopx.dto;

import lombok.Data;

/**
 * 库存检查响应DTO
 */
@Data
public class StockCheckResponseDTO {
    private Boolean available;
    private Integer stock;
    private Integer requestedQuantity;
}

