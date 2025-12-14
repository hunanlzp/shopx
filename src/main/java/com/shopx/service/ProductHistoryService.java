package com.shopx.service;

import com.shopx.entity.ProductHistory;

import java.util.List;

/**
 * 商品历史服务接口
 */
public interface ProductHistoryService {
    
    /**
     * 记录商品信息修改
     */
    void recordProductChange(Long productId, Long modifiedBy, String changes, String reason);
    
    /**
     * 获取商品修改历史
     */
    List<ProductHistory> getProductHistory(Long productId, int limit);
}

