package com.shopx.controller;

import com.shopx.annotation.ApiVersion;
import com.shopx.entity.ApiResponse;
import com.shopx.entity.Order;
import com.shopx.service.OrderService;
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
 * 订单控制器
 */
@Slf4j
@RestController
@RequestMapping("/orders")
@ApiVersion("v1")
@Tag(name = "订单管理", description = "订单相关API")
public class OrderController {
    
    @Autowired
    private OrderService orderService;
    
    /**
     * 创建订单
     */
    @Operation(summary = "创建订单", description = "从购物车创建订单")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "创建成功"),
            @ApiResponse(responseCode = "400", description = "参数错误")
    })
    @PostMapping
    public ResponseEntity<ApiResponse<Order>> createOrder(
            @Parameter(description = "购物车项ID列表", required = false) @RequestParam(required = false) List<Long> cartItemIds,
            @Parameter(description = "收货地址", required = true) @RequestParam String shippingAddress) {
        
        try {
            Long userId = SaTokenUtil.getCurrentUserId();
            if (userId == null) {
                return ResponseUtil.unauthorized("请先登录");
            }
            
            Order order = orderService.createOrder(userId, cartItemIds, shippingAddress);
            return ResponseUtil.success("订单创建成功", order);
        } catch (Exception e) {
            log.error("创建订单失败", e);
            return ResponseUtil.error("创建订单失败：" + e.getMessage());
        }
    }
    
    /**
     * 获取订单列表
     */
    @Operation(summary = "获取订单列表", description = "获取当前用户的订单列表")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "查询成功")
    })
    @GetMapping
    public ResponseEntity<ApiResponse<ResponseUtil.PageResult<Order>>> getOrders(
            @Parameter(description = "页码", required = false) @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页大小", required = false) @RequestParam(defaultValue = "20") int size) {
        
        try {
            Long userId = SaTokenUtil.getCurrentUserId();
            if (userId == null) {
                return ResponseUtil.unauthorized("请先登录");
            }
            
            ResponseUtil.PageResult<Order> result = orderService.getOrders(userId, page, size);
            return ResponseUtil.success("查询成功", result);
        } catch (Exception e) {
            log.error("获取订单列表失败", e);
            return ResponseUtil.error("获取订单列表失败，请稍后重试");
        }
    }
    
    /**
     * 获取订单详情
     */
    @Operation(summary = "获取订单详情", description = "根据订单ID获取订单详细信息")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "查询成功"),
            @ApiResponse(responseCode = "404", description = "订单不存在")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Order>> getOrderById(
            @Parameter(description = "订单ID", required = true) @PathVariable Long id) {
        
        try {
            Long userId = SaTokenUtil.getCurrentUserId();
            if (userId == null) {
                return ResponseUtil.unauthorized("请先登录");
            }
            
            Order order = orderService.getOrderById(id);
            
            // 检查权限
            if (!order.getUserId().equals(userId)) {
                return ResponseUtil.forbidden("无权访问此订单");
            }
            
            return ResponseUtil.success("查询成功", order);
        } catch (Exception e) {
            log.error("获取订单详情失败", e);
            return ResponseUtil.error("获取订单详情失败：" + e.getMessage());
        }
    }
    
    /**
     * 取消订单
     */
    @Operation(summary = "取消订单", description = "取消指定订单")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "取消成功"),
            @ApiResponse(responseCode = "400", description = "订单状态不允许取消")
    })
    @PostMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse<Void>> cancelOrder(
            @Parameter(description = "订单ID", required = true) @PathVariable Long id) {
        
        try {
            Long userId = SaTokenUtil.getCurrentUserId();
            if (userId == null) {
                return ResponseUtil.unauthorized("请先登录");
            }
            
            boolean success = orderService.cancelOrder(id, userId);
            return ResponseUtil.success("订单取消成功", null);
        } catch (Exception e) {
            log.error("取消订单失败", e);
            return ResponseUtil.error("取消订单失败：" + e.getMessage());
        }
    }
    
    /**
     * 支付订单
     */
    @Operation(summary = "支付订单", description = "支付指定订单")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "支付成功"),
            @ApiResponse(responseCode = "400", description = "订单状态不允许支付")
    })
    @PostMapping("/{id}/pay")
    public ResponseEntity<ApiResponse<Void>> payOrder(
            @Parameter(description = "订单ID", required = true) @PathVariable Long id,
            @Parameter(description = "支付方式", required = false) @RequestParam(defaultValue = "ALIPAY") String paymentMethod) {
        
        try {
            Long userId = SaTokenUtil.getCurrentUserId();
            if (userId == null) {
                return ResponseUtil.unauthorized("请先登录");
            }
            
            boolean success = orderService.payOrder(id, paymentMethod);
            return ResponseUtil.success("订单支付成功", null);
        } catch (Exception e) {
            log.error("支付订单失败", e);
            return ResponseUtil.error("支付订单失败：" + e.getMessage());
        }
    }
}

