package com.shopx.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 分页结果DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageResultDTO<T> {
    private List<T> list;
    private Long total;
    private Integer page;
    private Integer size;
    private Integer totalPages;
}

