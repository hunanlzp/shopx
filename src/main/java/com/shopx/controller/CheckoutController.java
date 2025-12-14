package com.shopx.controller;

import com.shopx.annotation.ApiVersion;
import com.shopx.entity.ApiResponse;
import com.shopx.entity.CartItem;
import com.shopx.entity.Order;
import com.shopx.service.CartService;
import com.shopx.service.OrderService;
import com.shopx.service.PriceService;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 结算控制器
 */
@Slf4j
@RestController
@RequestMapping("/checkout")
@ApiVersion("v1")
@Tag(name = "结算管理", description = "购物车结算相关API")
public class CheckoutController {
    
    @Autowired
    private CartService cartService;
    
    @Autowired
    private OrderService orderService;
    
    @Autowired
    private PriceService priceService;
    
    /**
     * 获取结算信息（3步流程：确认商品 → 选择地址 → 支付）
     */
    @Operation(summary = "获取结算信息", description = "获取购物车商品和总价信息")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "查询成功")
    })
    @GetMapping("/info")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getCheckoutInfo(
            @Parameter(description = "收货地址", required = false) @RequestParam(required = false) String shippingAddress) {
        
        try {
            Long userId = SaTokenUtil.getCurrentUserId();
            
            // 检查购物车商品状态
            List<CartItem> cartItems = cartService.checkCartItemsStatus(userId);
            
            // 计算总价
            BigDecimal totalAmount = BigDecimal.ZERO;
            BigDecimal totalShippingFee = BigDecimal.ZERO;
            BigDecimal totalTax = BigDecimal.ZERO;
            
            for (CartItem item : cartItems) {
                if ("VALID".equals(item.getStatus())) {
                    totalAmount = totalAmount.add(item.getSubtotal());
                    
                    // 计算运费和税费（简化实现）
                    Map<String, BigDecimal> priceInfo = priceService.calculateTotalPrice(
                            item.getProductId(), shippingAddress);
                    totalShippingFee = totalShippingFee.add(priceInfo.get("shippingFee"));
                    totalTax = totalTax.add(priceInfo.get("tax"));
                }
            }
            
            BigDecimal finalTotal = totalAmount.add(totalShippingFee).add(totalTax);
            
            Map<String, Object> result = new HashMap<>();
            result.put("cartItems", cartItems);
            result.put("subtotal", totalAmount);
            result.put("shippingFee", totalShippingFee);
            result.put("tax", totalTax);
            result.put("total", finalTotal);
            result.put("validItemCount", cartItems.stream()
                    .filter(item -> "VALID".equals(item.getStatus()))
                    .count());
            
            return ResponseUtil.success("获取结算信息成功", result);
        } catch (Exception e) {
            log.error("获取结算信息失败", e);
            return ResponseUtil.error("获取结算信息失败，请稍后重试");
        }
    }
    
    /**
     * 创建订单（简化流程）
     */
    @Operation(summary = "创建订单", description = "从购物车创建订单")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "创建成功"),
            @ApiResponse(responseCode = "400", description = "请求参数错误")
    })
    @PostMapping("/create-order")
    public ResponseEntity<ApiResponse<Order>> createOrder(
            @Parameter(description = "收货地址", required = true) @RequestParam String shippingAddress,
            @Parameter(description = "购物车项ID列表", required = false) @RequestParam(required = false) List<Long> cartItemIds) {
        
        try {
            Long userId = SaTokenUtil.getCurrentUserId();
            
            // 创建订单
            Order order = orderService.createOrder(userId, cartItemIds, shippingAddress);
            
            return ResponseUtil.success("订单创建成功", order);
        } catch (Exception e) {
            log.error("创建订单失败", e);
            return ResponseUtil.error("创建订单失败：" + e.getMessage());
        }
    }
    
    /**
     * 游客购买（可选注册）
     */
    @Operation(summary = "游客购买", description = "支持游客购买，可选择注册")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "创建成功")
    })
    @PostMapping("/guest-checkout")
    public ResponseEntity<ApiResponse<Order>> guestCheckout(
            @Parameter(description = "会话ID", required = true) @RequestParam String sessionId,
            @Parameter(description = "收货地址", required = true) @RequestParam String shippingAddress,
            @Parameter(description = "是否注册", required = false) @RequestParam(required = false) Boolean register) {
        
        try {
            // 获取游客购物车
            List<CartItem> guestCart = cartService.getGuestCart(sessionId);
            
            if (guestCart.isEmpty()) {
                return ResponseUtil.error("购物车为空");
            }
            
            // 如果选择注册，合并购物车
            if (Boolean.TRUE.equals(register)) {
                Long userId = SaTokenUtil.getCurrentUserId();
                if (userId != null) {
                    cartService.mergeGuestCartToUser(sessionId, userId);
                    Order order = orderService.createOrder(userId, null, shippingAddress);
                    return ResponseUtil.success("订单创建成功", order);
                }
            }
            
            // 游客购买（需要创建临时用户或使用游客订单）
            // 简化实现：返回错误提示需要注册
            return ResponseUtil.error("游客购买需要先注册，请先注册账号");
        } catch (Exception e) {
            log.error("游客购买失败", e);
            return ResponseUtil.error("游客购买失败：" + e.getMessage());
        }
    }
}

