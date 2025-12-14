package com.shopx.service;

import com.shopx.entity.CartItem;
import com.shopx.entity.Product;

import java.util.List;

/**
 * 购物车服务接口
 */
public interface CartService {
    
    /**
     * 获取购物车列表
     */
    List<CartItem> getCartItems(Long userId);
    
    /**
     * 添加商品到购物车
     */
    CartItem addToCart(Long userId, Long productId, Integer quantity);
    
    /**
     * 更新购物车商品数量
     */
    CartItem updateCartItem(Long userId, Long productId, Integer quantity);
    
    /**
     * 从购物车移除商品
     */
    void removeFromCart(Long userId, Long productId);
    
    /**
     * 清空购物车
     */
    void clearCart(Long userId);
    
    /**
     * 获取购物车商品总数
     */
    int getCartCount(Long userId);
    
    /**
     * 检查购物车中商品是否存在
     */
    boolean existsInCart(Long userId, Long productId);
    
    /**
     * 检查购物车商品状态（库存、价格、下架状态）
     */
    List<CartItem> checkCartItemsStatus(Long userId);
    
    /**
     * 获取游客购物车
     */
    List<CartItem> getGuestCart(String sessionId);
    
    /**
     * 添加商品到游客购物车
     */
    void addToGuestCart(String sessionId, Long productId, Integer quantity);
    
    /**
     * 合并游客购物车到用户购物车
     */
    void mergeGuestCartToUser(String sessionId, Long userId);
}

