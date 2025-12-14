package com.shopx.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shopx.entity.Order;
import com.shopx.entity.OrderItem;
import com.shopx.entity.ProductReview;
import com.shopx.entity.ReviewVote;
import com.shopx.mapper.OrderItemMapper;
import com.shopx.mapper.OrderMapper;
import com.shopx.mapper.ProductReviewMapper;
import com.shopx.mapper.ReviewVoteMapper;
import com.shopx.service.ProductReviewService;
import com.shopx.util.ResponseUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 商品评价服务实现类
 */
@Slf4j
@Service
public class ProductReviewServiceImpl extends ServiceImpl<ProductReviewMapper, ProductReview> implements ProductReviewService {
    
    @Autowired
    private ProductReviewMapper reviewMapper;
    
    @Autowired
    private OrderMapper orderMapper;
    
    @Autowired
    private OrderItemMapper orderItemMapper;
    
    @Autowired(required = false)
    private ReviewVoteMapper reviewVoteMapper;
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ProductReview createReview(Long userId, Long productId, Long orderId, Integer rating, String content, String images, String videos) {
        log.info("创建评价: userId={}, productId={}, orderId={}", userId, productId, orderId);
        
        // 验证是否购买
        if (orderId != null) {
            boolean verified = verifyPurchase(userId, productId);
            if (!verified) {
                throw new RuntimeException("只有购买后才能评价");
            }
        }
        
        ProductReview review = new ProductReview();
        review.setUserId(userId);
        review.setProductId(productId);
        review.setOrderId(orderId);
        review.setRating(rating);
        review.setContent(content);
        review.setImages(images);
        review.setVideos(videos);
        review.setHelpfulCount(0);
        review.setIsVerified(orderId != null);
        
        reviewMapper.insert(review);
        return review;
    }
    
    @Override
    public ResponseUtil.PageResult<ProductReview> getProductReviews(Long productId, int page, int size, String sortBy, String order) {
        QueryWrapper<ProductReview> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("product_id", productId);
        
        if ("rating".equals(sortBy)) {
            if ("asc".equalsIgnoreCase(order)) {
                queryWrapper.orderByAsc("rating");
            } else {
                queryWrapper.orderByDesc("rating");
            }
        } else if ("helpful".equals(sortBy)) {
            queryWrapper.orderByDesc("helpful_count");
        } else {
            queryWrapper.orderByDesc("create_time");
        }
        
        Page<ProductReview> pageParam = new Page<>(page, size);
        Page<ProductReview> result = reviewMapper.selectPage(pageParam, queryWrapper);
        
        return ResponseUtil.PageResult.<ProductReview>builder()
                .data(result.getRecords())
                .total(result.getTotal())
                .page(page)
                .size(size)
                .totalPages((int) result.getPages())
                .build();
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean voteHelpful(Long reviewId, Long userId, boolean helpful) {
        ProductReview review = reviewMapper.selectById(reviewId);
        if (review == null) {
            return false;
        }
        
        // 检查用户是否已投票
        if (reviewVoteMapper != null) {
            QueryWrapper<ReviewVote> voteWrapper = new QueryWrapper<>();
            voteWrapper.eq("review_id", reviewId)
                      .eq("user_id", userId);
            ReviewVote existingVote = reviewVoteMapper.selectOne(voteWrapper);
            
            if (existingVote != null) {
                // 如果已投票，取消之前的投票
                if (existingVote.getVoteType().equals("HELPFUL") && helpful) {
                    return true; // 已经投过有用票
                }
                if (existingVote.getVoteType().equals("NOT_HELPFUL") && !helpful) {
                    return true; // 已经投过无用票
                }
                
                // 更新投票
                if (helpful && existingVote.getVoteType().equals("NOT_HELPFUL")) {
                    // 从无用改为有用
                    review.setHelpfulCount(review.getHelpfulCount() + 2);
                    existingVote.setVoteType("HELPFUL");
                    reviewVoteMapper.updateById(existingVote);
                } else if (!helpful && existingVote.getVoteType().equals("HELPFUL")) {
                    // 从有用改为无用
                    review.setHelpfulCount(Math.max(0, review.getHelpfulCount() - 1));
                    existingVote.setVoteType("NOT_HELPFUL");
                    reviewVoteMapper.updateById(existingVote);
                }
            } else {
                // 创建新投票
                ReviewVote vote = new ReviewVote();
                vote.setReviewId(reviewId);
                vote.setUserId(userId);
                vote.setVoteType(helpful ? "HELPFUL" : "NOT_HELPFUL");
                reviewVoteMapper.insert(vote);
                
                if (helpful) {
                    review.setHelpfulCount(review.getHelpfulCount() + 1);
                }
            }
        } else {
            // 如果没有ReviewVoteMapper，简单增加计数
            if (helpful) {
                review.setHelpfulCount(review.getHelpfulCount() + 1);
            }
        }
        
        reviewMapper.updateById(review);
        return true;
    }
    
    @Override
    public boolean hasUserVoted(Long reviewId, Long userId) {
        if (reviewVoteMapper == null) {
            return false;
        }
        
        QueryWrapper<ReviewVote> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("review_id", reviewId)
                   .eq("user_id", userId);
        return reviewVoteMapper.selectCount(queryWrapper) > 0;
    }
    
    @Override
    public boolean merchantReply(Long reviewId, String reply) {
        ProductReview review = reviewMapper.selectById(reviewId);
        if (review == null) {
            return false;
        }
        
        review.setMerchantReply(reply);
        review.setReplyTime(LocalDateTime.now());
        reviewMapper.updateById(review);
        return true;
    }
    
    @Override
    public Map<String, Object> getReviewStats(Long productId) {
        QueryWrapper<ProductReview> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("product_id", productId);
        
        List<ProductReview> reviews = reviewMapper.selectList(queryWrapper);
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalReviews", reviews.size());
        
        if (!reviews.isEmpty()) {
            double avgRating = reviews.stream()
                    .mapToInt(ProductReview::getRating)
                    .average()
                    .orElse(0.0);
            stats.put("averageRating", avgRating);
            
            // 评分分布
            Map<Integer, Long> ratingDistribution = new HashMap<>();
            for (int i = 1; i <= 5; i++) {
                final int rating = i;
                long count = reviews.stream()
                        .filter(r -> r.getRating() == rating)
                        .count();
                ratingDistribution.put(rating, count);
            }
            stats.put("ratingDistribution", ratingDistribution);
        } else {
            stats.put("averageRating", 0.0);
            stats.put("ratingDistribution", new HashMap<>());
        }
        
        return stats;
    }
    
    @Override
    public boolean verifyPurchase(Long userId, Long productId) {
        // 检查用户是否有包含该商品的已完成订单
        QueryWrapper<Order> orderWrapper = new QueryWrapper<>();
        orderWrapper.eq("user_id", userId)
                   .eq("status", "DELIVERED");
        List<Order> orders = orderMapper.selectList(orderWrapper);
        
        for (Order order : orders) {
            QueryWrapper<OrderItem> itemWrapper = new QueryWrapper<>();
            itemWrapper.eq("order_id", order.getId())
                      .eq("product_id", productId);
            OrderItem item = orderItemMapper.selectOne(itemWrapper);
            if (item != null) {
                return true;
            }
        }
        
        return false;
    }
}

