package com.shopx.controller;

import com.shopx.annotation.ApiVersion;
import com.shopx.entity.ApiResponse;
import com.shopx.entity.ProductReview;
import com.shopx.service.ProductReviewService;
import com.shopx.util.ResponseUtil;
import com.shopx.util.SaTokenUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 商品评价控制器
 */
@Slf4j
@RestController
@RequestMapping("/reviews")
@ApiVersion("v1")
@Tag(name = "商品评价", description = "商品评价相关API")
public class ProductReviewController {
    
    @Autowired
    private ProductReviewService reviewService;
    
    /**
     * 创建评价
     */
    @Operation(summary = "创建评价", description = "创建商品评价（需要购买验证）")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "创建成功")
    })
    @PostMapping
    public ResponseEntity<ApiResponse<ProductReview>> createReview(
            @Parameter(description = "商品ID", required = true) @RequestParam Long productId,
            @Parameter(description = "订单ID", required = false) @RequestParam(required = false) Long orderId,
            @Parameter(description = "评分", required = true) @RequestParam Integer rating,
            @Parameter(description = "评价内容", required = false) @RequestParam(required = false) String content,
            @Parameter(description = "评价图片（JSON数组）", required = false) @RequestParam(required = false) String images,
            @Parameter(description = "评价视频（JSON数组）", required = false) @RequestParam(required = false) String videos) {
        
        try {
            Long userId = SaTokenUtil.getCurrentUserId();
            ProductReview review = reviewService.createReview(userId, productId, orderId, rating, content, images, videos);
            return ResponseUtil.success("评价创建成功", review);
        } catch (Exception e) {
            log.error("创建评价失败", e);
            return ResponseUtil.error("创建评价失败：" + e.getMessage());
        }
    }
    
    /**
     * 获取商品评价列表
     */
    @Operation(summary = "获取评价列表", description = "获取商品评价列表，支持筛选和排序")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "查询成功")
    })
    @GetMapping("/product/{productId}")
    public ResponseEntity<ApiResponse<ResponseUtil.PageResult<ProductReview>>> getProductReviews(
            @Parameter(description = "商品ID", required = true) @PathVariable Long productId,
            @Parameter(description = "页码", required = false) @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页大小", required = false) @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "排序方式", required = false) @RequestParam(required = false) String sortBy,
            @Parameter(description = "排序顺序", required = false) @RequestParam(required = false) String order) {
        
        try {
            ResponseUtil.PageResult<ProductReview> result = reviewService.getProductReviews(
                    productId, page, size, sortBy, order);
            return ResponseUtil.success("查询成功", result);
        } catch (Exception e) {
            log.error("获取评价列表失败", e);
            return ResponseUtil.error("获取评价列表失败，请稍后重试");
        }
    }
    
    /**
     * 评价有用性投票
     */
    @Operation(summary = "评价有用性投票", description = "对评价进行有用性投票")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "投票成功")
    })
    @PostMapping("/{reviewId}/vote")
    public ResponseEntity<ApiResponse<Void>> voteHelpful(
            @Parameter(description = "评价ID", required = true) @PathVariable Long reviewId,
            @Parameter(description = "是否有用", required = true) @RequestParam boolean helpful) {
        
        try {
            Long userId = SaTokenUtil.getCurrentUserId();
            boolean success = reviewService.voteHelpful(reviewId, userId, helpful);
            if (success) {
                return ResponseUtil.success("投票成功", null);
            } else {
                return ResponseUtil.error("投票失败，评价不存在");
            }
        } catch (Exception e) {
            log.error("投票失败", e);
            return ResponseUtil.error("投票失败，请稍后重试");
        }
    }
    
    /**
     * 获取商品评价统计
     */
    @Operation(summary = "获取评价统计", description = "获取商品评价统计数据")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "查询成功")
    })
    @GetMapping("/product/{productId}/stats")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getReviewStats(
            @Parameter(description = "商品ID", required = true) @PathVariable Long productId) {
        
        try {
            Map<String, Object> stats = reviewService.getReviewStats(productId);
            return ResponseUtil.success("查询成功", stats);
        } catch (Exception e) {
            log.error("获取评价统计失败", e);
            return ResponseUtil.error("获取评价统计失败，请稍后重试");
        }
    }
    
    /**
     * 商家回复评价
     */
    @Operation(summary = "商家回复", description = "商家回复用户评价")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "回复成功")
    })
    @PostMapping("/{reviewId}/reply")
    public ResponseEntity<ApiResponse<Void>> merchantReply(
            @Parameter(description = "评价ID", required = true) @PathVariable Long reviewId,
            @Parameter(description = "回复内容", required = true) @RequestParam String reply) {
        
        try {
            boolean success = reviewService.merchantReply(reviewId, reply);
            if (success) {
                return ResponseUtil.success("回复成功", null);
            } else {
                return ResponseUtil.error("回复失败，评价不存在");
            }
        } catch (Exception e) {
            log.error("回复失败", e);
            return ResponseUtil.error("回复失败，请稍后重试");
        }
    }
}

