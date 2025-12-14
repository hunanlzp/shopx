package com.shopx.service;

import com.shopx.entity.ProductImage;

import java.util.List;

/**
 * 商品图片服务接口
 */
public interface ProductImageService {
    
    /**
     * 添加商品图片
     */
    ProductImage addProductImage(Long productId, String imageUrl, String imageType, Integer sortOrder);
    
    /**
     * 获取商品图片列表
     */
    List<ProductImage> getProductImages(Long productId);
    
    /**
     * 删除商品图片
     */
    boolean deleteProductImage(Long imageId);
    
    /**
     * 更新图片排序
     */
    boolean updateImageOrder(Long imageId, Integer sortOrder);
    
    /**
     * 检查商品图片数量（至少3张）
     */
    boolean checkImageCount(Long productId, int minCount);
}

