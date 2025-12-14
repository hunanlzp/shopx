package com.shopx.controller;

import com.shopx.annotation.ApiVersion;
import com.shopx.entity.ApiResponse;
import com.shopx.entity.CartItem;
import com.shopx.service.CartService;
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
 * 购物车控制器
 */
@Slf4j
@RestController
@RequestMapping("/cart")
@ApiVersion("v1")
@Tag(name = "购物车管理", description = "购物车相关API")
public class CartController {
    
    @Autowired
    private CartService cartService;
    
    /**
     * 获取购物车列表
     */
    @Operation(summary = "获取购物车", description = "获取当前用户的购物车列表")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "查询成功")
    })
    @GetMapping
    public ResponseEntity<ApiResponse<List<CartItem>>> getCart() {
        try {
            Long userId = SaTokenUtil.getCurrentUserId();
            if (userId == null) {
                return ResponseUtil.unauthorized("请先登录");
            }
            
            List<CartItem> cartItems = cartService.getCartItems(userId);
            return ResponseUtil.success("查询成功", cartItems);
        } catch (Exception e) {
            log.error("获取购物车失败", e);
            return ResponseUtil.error("获取购物车失败，请稍后重试");
        }
    }
    
    /**
     * 添加商品到购物车
     */
    @Operation(summary = "添加商品到购物车", description = "将商品添加到购物车")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "添加成功"),
            @ApiResponse(responseCode = "400", description = "参数错误")
    })
    @PostMapping("/add")
    public ResponseEntity<ApiResponse<CartItem>> addToCart(
            @Parameter(description = "商品ID", required = true) @RequestParam Long productId,
            @Parameter(description = "数量", required = false) @RequestParam(defaultValue = "1") Integer quantity) {
        
        try {
            Long userId = SaTokenUtil.getCurrentUserId();
            if (userId == null) {
                return ResponseUtil.unauthorized("请先登录");
            }
            
            CartItem cartItem = cartService.addToCart(userId, productId, quantity);
            return ResponseUtil.success("添加成功", cartItem);
        } catch (Exception e) {
            log.error("添加商品到购物车失败", e);
            return ResponseUtil.error("添加失败：" + e.getMessage());
        }
    }
    
    /**
     * 更新购物车商品数量
     */
    @Operation(summary = "更新购物车商品数量", description = "更新购物车中商品的数量")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "更新成功"),
            @ApiResponse(responseCode = "404", description = "商品不存在")
    })
    @PutMapping("/update")
    public ResponseEntity<ApiResponse<CartItem>> updateCartItem(
            @Parameter(description = "商品ID", required = true) @RequestParam Long productId,
            @Parameter(description = "数量", required = true) @RequestParam Integer quantity) {
        
        try {
            Long userId = SaTokenUtil.getCurrentUserId();
            if (userId == null) {
                return ResponseUtil.unauthorized("请先登录");
            }
            
            CartItem cartItem = cartService.updateCartItem(userId, productId, quantity);
            return ResponseUtil.success("更新成功", cartItem);
        } catch (Exception e) {
            log.error("更新购物车失败", e);
            return ResponseUtil.error("更新失败：" + e.getMessage());
        }
    }
    
    /**
     * 从购物车移除商品
     */
    @Operation(summary = "移除购物车商品", description = "从购物车中移除指定商品")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "移除成功")
    })
    @DeleteMapping("/remove")
    public ResponseEntity<ApiResponse<Void>> removeFromCart(
            @Parameter(description = "商品ID", required = true) @RequestParam Long productId) {
        
        try {
            Long userId = SaTokenUtil.getCurrentUserId();
            if (userId == null) {
                return ResponseUtil.unauthorized("请先登录");
            }
            
            cartService.removeFromCart(userId, productId);
            return ResponseUtil.success("移除成功", null);
        } catch (Exception e) {
            log.error("移除购物车商品失败", e);
            return ResponseUtil.error("移除失败，请稍后重试");
        }
    }
    
    /**
     * 清空购物车
     */
    @Operation(summary = "清空购物车", description = "清空当前用户的所有购物车商品")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "清空成功")
    })
    @DeleteMapping("/clear")
    public ResponseEntity<ApiResponse<Void>> clearCart() {
        try {
            Long userId = SaTokenUtil.getCurrentUserId();
            if (userId == null) {
                return ResponseUtil.unauthorized("请先登录");
            }
            
            cartService.clearCart(userId);
            return ResponseUtil.success("清空成功", null);
        } catch (Exception e) {
            log.error("清空购物车失败", e);
            return ResponseUtil.error("清空失败，请稍后重试");
        }
    }
    
    /**
     * 获取购物车商品总数
     */
    @Operation(summary = "获取购物车商品总数", description = "获取当前用户购物车中商品的总数量")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "查询成功")
    })
    @GetMapping("/count")
    public ResponseEntity<ApiResponse<Integer>> getCartCount() {
        try {
            Long userId = SaTokenUtil.getCurrentUserId();
            if (userId == null) {
                return ResponseUtil.success("购物车为空", 0);
            }
            
            int count = cartService.getCartCount(userId);
            return ResponseUtil.success("查询成功", count);
        } catch (Exception e) {
            log.error("获取购物车数量失败", e);
            return ResponseUtil.error("查询失败，请稍后重试");
        }
    }
}

