package com.shopx.service;

import com.shopx.entity.Product;
import com.shopx.entity.Wishlist;
import com.shopx.util.ResponseUtil;

import java.util.List;
import java.util.Map;

/**
 * 愿望清单服务接口
 */
public interface WishlistService {
    
    /**
     * 添加到愿望清单
     */
    Wishlist addToWishlist(Long userId, Long productId, String category, String notes);
    
    /**
     * 从愿望清单移除
     */
    boolean removeFromWishlist(Long userId, Long productId);
    
    /**
     * 获取用户愿望清单
     */
    ResponseUtil.PageResult<Wishlist> getUserWishlist(Long userId, String category, int page, int size);
    
    /**
     * 设置价格提醒
     */
    boolean setPriceAlert(Long userId, Long productId, BigDecimal targetPrice);
    
    /**
     * 检查价格下降并发送提醒
     */
    List<Wishlist> checkPriceDrops();
    
    /**
     * 批量操作（删除、移动分类）
     */
    boolean batchOperation(Long userId, List<Long> wishlistIds, String operation, String targetCategory);
    
    /**
     * 分享愿望清单
     */
    String shareWishlist(Long userId, String category);
    
    /**
     * 通过分享链接获取愿望清单
     */
    List<Product> getWishlistByShareLink(String shareLink);
}

