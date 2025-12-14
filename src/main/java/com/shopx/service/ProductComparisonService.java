package com.shopx.service;

import com.shopx.dto.ComparisonTableDTO;
import com.shopx.entity.Product;
import com.shopx.entity.ProductComparison;

import java.util.List;

/**
 * 商品对比服务接口
 */
public interface ProductComparisonService {
    
    /**
     * 创建商品对比列表（最多5个商品）
     */
    ProductComparison createComparison(Long userId, String comparisonName, List<Long> productIds, Boolean isPublic);
    
    /**
     * 获取商品对比详情
     */
    ComparisonTableDTO getComparisonDetails(Long comparisonId);
    
    /**
     * 获取用户的对比列表
     */
    List<ProductComparison> getUserComparisons(Long userId);
    
    /**
     * 通过分享链接获取对比列表
     */
    ProductComparison getComparisonByShareLink(String shareLink);
    
    /**
     * 更新对比列表
     */
    ProductComparison updateComparison(Long comparisonId, Long userId, List<Long> productIds);
    
    /**
     * 删除对比列表
     */
    boolean deleteComparison(Long comparisonId, Long userId);
    
    /**
     * 生成对比表格数据
     */
    ComparisonTableDTO generateComparisonTable(List<Long> productIds);
}

