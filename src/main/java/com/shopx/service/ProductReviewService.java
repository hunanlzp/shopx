package com.shopx.service;

import com.shopx.entity.ProductReview;
import com.shopx.util.ResponseUtil;

import java.util.List;
import java.util.Map;

/**
 * 商品评价服务接口
 */
public interface ProductReviewService {
    
    /**
     * 创建评价（需要验证是否购买）
     */
    ProductReview createReview(Long userId, Long productId, Long orderId, Integer rating, String content, String images, String videos);
    
    /**
     * 获取商品评价列表
     */
    ResponseUtil.PageResult<ProductReview> getProductReviews(Long productId, int page, int size, String sortBy, String order);
    
    /**
     * 评价有用性投票
     */
    boolean voteHelpful(Long reviewId, Long userId, boolean helpful);
    
    /**
     * 检查用户是否已投票
     */
    boolean hasUserVoted(Long reviewId, Long userId);
    
    /**
     * 商家回复评价
     */
    boolean merchantReply(Long reviewId, String reply);
    
    /**
     * 获取商品评价统计
     */
    Map<String, Object> getReviewStats(Long productId);
    
    /**
     * 验证用户是否已购买该商品
     */
    boolean verifyPurchase(Long userId, Long productId);
}

