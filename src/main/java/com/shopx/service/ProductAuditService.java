package com.shopx.service;

import com.shopx.entity.Product;
import com.shopx.entity.ProductAudit;
import com.shopx.util.ResponseUtil;

import java.util.List;

/**
 * 商品审核服务接口
 */
public interface ProductAuditService {
    
    /**
     * 计算商品完整度评分
     */
    int calculateCompletenessScore(Product product);
    
    /**
     * 提交审核
     */
    ProductAudit submitForAudit(Long productId);
    
    /**
     * 审核商品
     */
    ProductAudit auditProduct(Long productId, Long auditorId, String status, String comment);
    
    /**
     * 获取待审核商品列表
     */
    ResponseUtil.PageResult<Product> getPendingAuditProducts(int page, int size);
    
    /**
     * 获取商品审核历史
     */
    List<ProductAudit> getProductAuditHistory(Long productId);
}

