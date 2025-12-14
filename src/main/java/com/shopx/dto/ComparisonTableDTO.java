package com.shopx.dto;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 对比表格DTO
 */
@Data
public class ComparisonTableDTO {
    private List<ProductComparisonItemDTO> products;
    private List<AttributeComparisonDTO> attributes;

    @Data
    public static class ProductComparisonItemDTO {
        private Long id;
        private String name;
        private Map<String, Object> properties;
    }

    @Data
    public static class AttributeComparisonDTO {
        private String name;
        private List<Object> values;
    }
}

