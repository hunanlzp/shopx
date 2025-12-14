package com.shopx.controller;

import com.shopx.annotation.ApiVersion;
import com.shopx.entity.ApiResponse;
import com.shopx.entity.Product;
import com.shopx.entity.Wishlist;
import com.shopx.service.WishlistService;
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

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 愿望清单控制器
 */
@Slf4j
@RestController
@RequestMapping("/wishlist")
@ApiVersion("v1")
@Tag(name = "愿望清单", description = "愿望清单相关API")
public class WishlistController {
    
    @Autowired
    private WishlistService wishlistService;
    
    /**
     * 添加到愿望清单
     */
    @Operation(summary = "添加到愿望清单", description = "将商品添加到愿望清单")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "添加成功")
    })
    @PostMapping
    public ResponseEntity<ApiResponse<Wishlist>> addToWishlist(
            @Parameter(description = "商品ID", required = true) @RequestParam Long productId,
            @Parameter(description = "分类", required = false) @RequestParam(required = false) String category,
            @Parameter(description = "备注", required = false) @RequestParam(required = false) String notes) {
        
        try {
            Long userId = SaTokenUtil.getCurrentUserId();
            Wishlist wishlist = wishlistService.addToWishlist(userId, productId, category, notes);
            return ResponseUtil.success("添加成功", wishlist);
        } catch (Exception e) {
            log.error("添加到愿望清单失败", e);
            return ResponseUtil.error("添加到愿望清单失败：" + e.getMessage());
        }
    }
    
    /**
     * 从愿望清单移除
     */
    @Operation(summary = "从愿望清单移除", description = "从愿望清单中移除商品")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "移除成功")
    })
    @DeleteMapping("/{productId}")
    public ResponseEntity<ApiResponse<Void>> removeFromWishlist(
            @Parameter(description = "商品ID", required = true) @PathVariable Long productId) {
        
        try {
            Long userId = SaTokenUtil.getCurrentUserId();
            boolean success = wishlistService.removeFromWishlist(userId, productId);
            if (success) {
                return ResponseUtil.success("移除成功", null);
            } else {
                return ResponseUtil.error("移除失败，商品不在愿望清单中");
            }
        } catch (Exception e) {
            log.error("从愿望清单移除失败", e);
            return ResponseUtil.error("从愿望清单移除失败，请稍后重试");
        }
    }
    
    /**
     * 获取用户愿望清单
     */
    @Operation(summary = "获取愿望清单", description = "获取当前用户的愿望清单")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "查询成功")
    })
    @GetMapping
    public ResponseEntity<ApiResponse<ResponseUtil.PageResult<Wishlist>>> getUserWishlist(
            @Parameter(description = "分类", required = false) @RequestParam(required = false) String category,
            @Parameter(description = "页码", required = false) @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页大小", required = false) @RequestParam(defaultValue = "20") int size) {
        
        try {
            Long userId = SaTokenUtil.getCurrentUserId();
            ResponseUtil.PageResult<Wishlist> result = wishlistService.getUserWishlist(userId, category, page, size);
            return ResponseUtil.success("查询成功", result);
        } catch (Exception e) {
            log.error("获取愿望清单失败", e);
            return ResponseUtil.error("获取愿望清单失败，请稍后重试");
        }
    }
    
    /**
     * 设置价格提醒
     */
    @Operation(summary = "设置价格提醒", description = "设置商品价格下降提醒")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "设置成功")
    })
    @PostMapping("/price-alert")
    public ResponseEntity<ApiResponse<Void>> setPriceAlert(
            @Parameter(description = "商品ID", required = true) @RequestParam Long productId,
            @Parameter(description = "目标价格", required = true) @RequestParam BigDecimal targetPrice) {
        
        try {
            Long userId = SaTokenUtil.getCurrentUserId();
            boolean success = wishlistService.setPriceAlert(userId, productId, targetPrice);
            if (success) {
                return ResponseUtil.success("设置成功", null);
            } else {
                return ResponseUtil.error("设置失败，商品不在愿望清单中");
            }
        } catch (Exception e) {
            log.error("设置价格提醒失败", e);
            return ResponseUtil.error("设置价格提醒失败，请稍后重试");
        }
    }
    
    /**
     * 批量操作
     */
    @Operation(summary = "批量操作", description = "批量删除或移动分类")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "操作成功")
    })
    @PostMapping("/batch")
    public ResponseEntity<ApiResponse<Void>> batchOperation(
            @Parameter(description = "愿望清单ID列表", required = true) @RequestBody List<Long> wishlistIds,
            @Parameter(description = "操作类型", required = true) @RequestParam String operation,
            @Parameter(description = "目标分类（移动操作需要）", required = false) @RequestParam(required = false) String targetCategory) {
        
        try {
            Long userId = SaTokenUtil.getCurrentUserId();
            boolean success = wishlistService.batchOperation(userId, wishlistIds, operation, targetCategory);
            if (success) {
                return ResponseUtil.success("操作成功", null);
            } else {
                return ResponseUtil.error("操作失败");
            }
        } catch (Exception e) {
            log.error("批量操作失败", e);
            return ResponseUtil.error("批量操作失败，请稍后重试");
        }
    }
    
    /**
     * 分享愿望清单
     */
    @Operation(summary = "分享愿望清单", description = "生成愿望清单分享链接")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "分享成功")
    })
    @PostMapping("/share")
    public ResponseEntity<ApiResponse<Map<String, String>>> shareWishlist(
            @Parameter(description = "分类（可选）", required = false) @RequestParam(required = false) String category) {
        
        try {
            Long userId = SaTokenUtil.getCurrentUserId();
            String shareLink = wishlistService.shareWishlist(userId, category);
            Map<String, String> result = Map.of("shareLink", shareLink);
            return ResponseUtil.success("分享成功", result);
        } catch (Exception e) {
            log.error("分享愿望清单失败", e);
            return ResponseUtil.error("分享愿望清单失败，请稍后重试");
        }
    }
    
    /**
     * 通过分享链接获取愿望清单
     */
    @Operation(summary = "通过分享链接获取愿望清单", description = "通过分享链接查看愿望清单")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "查询成功")
    })
    @GetMapping("/share/{shareLink}")
    public ResponseEntity<ApiResponse<List<Product>>> getWishlistByShareLink(
            @Parameter(description = "分享链接", required = true) @PathVariable String shareLink) {
        
        try {
            List<Product> products = wishlistService.getWishlistByShareLink(shareLink);
            return ResponseUtil.success("查询成功", products);
        } catch (Exception e) {
            log.error("通过分享链接获取愿望清单失败", e);
            return ResponseUtil.error("获取愿望清单失败，请稍后重试");
        }
    }
}

