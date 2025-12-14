package com.shopx.controller;

import com.shopx.annotation.ApiVersion;
import com.shopx.entity.ApiResponse;
import com.shopx.entity.Product;
import com.shopx.entity.ProductReservation;
import com.shopx.entity.StockNotification;
import com.shopx.service.StockService;
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

/**
 * 库存管理控制器
 */
@Slf4j
@RestController
@RequestMapping("/stock")
@ApiVersion("v1")
@Tag(name = "库存管理", description = "库存提醒和预订相关API")
public class StockController {
    
    @Autowired
    private StockService stockService;
    
    /**
     * 检查库存
     */
    @Operation(summary = "检查库存", description = "实时检查商品库存")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "检查成功")
    })
    @GetMapping("/check")
    public ResponseEntity<ApiResponse<Boolean>> checkStock(
            @Parameter(description = "商品ID", required = true) @RequestParam Long productId,
            @Parameter(description = "数量", required = true) @RequestParam Integer quantity) {
        
        try {
            boolean available = stockService.checkAndSyncStock(productId, quantity);
            return ResponseUtil.success("检查成功", available);
        } catch (Exception e) {
            log.error("检查库存失败", e);
            return ResponseUtil.error("检查库存失败，请稍后重试");
        }
    }
    
    /**
     * 添加缺货提醒
     */
    @Operation(summary = "添加缺货提醒", description = "当商品缺货时添加提醒，到货后通知")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "添加成功")
    })
    @PostMapping("/notification")
    public ResponseEntity<ApiResponse<StockNotification>> addStockNotification(
            @Parameter(description = "商品ID", required = true) @RequestParam Long productId) {
        
        try {
            Long userId = SaTokenUtil.getCurrentUserId();
            StockNotification notification = stockService.addStockNotification(userId, productId);
            return ResponseUtil.success("添加成功", notification);
        } catch (Exception e) {
            log.error("添加缺货提醒失败", e);
            return ResponseUtil.error("添加缺货提醒失败：" + e.getMessage());
        }
    }
    
    /**
     * 获取用户的缺货提醒列表
     */
    @Operation(summary = "获取缺货提醒列表", description = "获取当前用户的所有缺货提醒")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "查询成功")
    })
    @GetMapping("/notifications")
    public ResponseEntity<ApiResponse<List<StockNotification>>> getUserStockNotifications() {
        
        try {
            Long userId = SaTokenUtil.getCurrentUserId();
            List<StockNotification> notifications = stockService.getUserStockNotifications(userId);
            return ResponseUtil.success("查询成功", notifications);
        } catch (Exception e) {
            log.error("获取缺货提醒列表失败", e);
            return ResponseUtil.error("获取缺货提醒列表失败，请稍后重试");
        }
    }
    
    /**
     * 取消缺货提醒
     */
    @Operation(summary = "取消缺货提醒", description = "取消指定的缺货提醒")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "取消成功")
    })
    @DeleteMapping("/notification/{notificationId}")
    public ResponseEntity<ApiResponse<Void>> cancelStockNotification(
            @Parameter(description = "提醒ID", required = true) @PathVariable Long notificationId) {
        
        try {
            Long userId = SaTokenUtil.getCurrentUserId();
            boolean success = stockService.cancelStockNotification(notificationId, userId);
            if (success) {
                return ResponseUtil.success("取消成功", null);
            } else {
                return ResponseUtil.error("取消失败，提醒不存在或无权限");
            }
        } catch (Exception e) {
            log.error("取消缺货提醒失败", e);
            return ResponseUtil.error("取消缺货提醒失败，请稍后重试");
        }
    }
    
    /**
     * 创建商品预订
     */
    @Operation(summary = "创建商品预订", description = "预订缺货商品，到货后自动处理")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "预订成功")
    })
    @PostMapping("/reservation")
    public ResponseEntity<ApiResponse<ProductReservation>> createReservation(
            @Parameter(description = "商品ID", required = true) @RequestParam Long productId,
            @Parameter(description = "预订数量", required = true) @RequestParam Integer quantity) {
        
        try {
            Long userId = SaTokenUtil.getCurrentUserId();
            ProductReservation reservation = stockService.createReservation(userId, productId, quantity);
            return ResponseUtil.success("预订成功", reservation);
        } catch (Exception e) {
            log.error("创建商品预订失败", e);
            return ResponseUtil.error("创建商品预订失败：" + e.getMessage());
        }
    }
    
    /**
     * 获取用户预订列表
     */
    @Operation(summary = "获取预订列表", description = "获取当前用户的所有商品预订")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "查询成功")
    })
    @GetMapping("/reservations")
    public ResponseEntity<ApiResponse<List<ProductReservation>>> getUserReservations() {
        
        try {
            Long userId = SaTokenUtil.getCurrentUserId();
            List<ProductReservation> reservations = stockService.getUserReservations(userId);
            return ResponseUtil.success("查询成功", reservations);
        } catch (Exception e) {
            log.error("获取预订列表失败", e);
            return ResponseUtil.error("获取预订列表失败，请稍后重试");
        }
    }
    
    /**
     * 取消预订
     */
    @Operation(summary = "取消预订", description = "取消指定的商品预订")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "取消成功")
    })
    @DeleteMapping("/reservation/{reservationId}")
    public ResponseEntity<ApiResponse<Void>> cancelReservation(
            @Parameter(description = "预订ID", required = true) @PathVariable Long reservationId) {
        
        try {
            Long userId = SaTokenUtil.getCurrentUserId();
            boolean success = stockService.cancelReservation(reservationId, userId);
            if (success) {
                return ResponseUtil.success("取消成功", null);
            } else {
                return ResponseUtil.error("取消失败，预订不存在或无权限");
            }
        } catch (Exception e) {
            log.error("取消预订失败", e);
            return ResponseUtil.error("取消预订失败，请稍后重试");
        }
    }
    
    /**
     * 获取替代商品推荐
     */
    @Operation(summary = "获取替代商品", description = "当商品缺货时，推荐替代商品")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "查询成功")
    })
    @GetMapping("/alternatives/{productId}")
    public ResponseEntity<ApiResponse<List<Product>>> getAlternativeProducts(
            @Parameter(description = "商品ID", required = true) @PathVariable Long productId,
            @Parameter(description = "数量限制", required = false) @RequestParam(defaultValue = "5") int limit) {
        
        try {
            List<Product> alternatives = stockService.getAlternativeProducts(productId, limit);
            return ResponseUtil.success("查询成功", alternatives);
        } catch (Exception e) {
            log.error("获取替代商品失败", e);
            return ResponseUtil.error("获取替代商品失败，请稍后重试");
        }
    }
}

