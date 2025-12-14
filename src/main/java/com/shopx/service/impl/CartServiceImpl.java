package com.shopx.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shopx.entity.CartItem;
import com.shopx.entity.Product;
import com.shopx.exception.BusinessException;
import com.shopx.mapper.CartItemMapper;
import com.shopx.service.CartService;
import com.shopx.service.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 购物车服务实现类
 */
@Slf4j
@Service
public class CartServiceImpl extends ServiceImpl<CartItemMapper, CartItem> implements CartService {
    
    @Autowired
    private CartItemMapper cartItemMapper;
    
    @Autowired
    private ProductService productService;
    
    @Override
    public List<CartItem> getCartItems(Long userId) {
        log.info("获取购物车列表: userId={}", userId);
        
        QueryWrapper<CartItem> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId)
                   .orderByDesc("create_time");
        
        return cartItemMapper.selectList(queryWrapper);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public CartItem addToCart(Long userId, Long productId, Integer quantity) {
        log.info("添加商品到购物车: userId={}, productId={}, quantity={}", userId, productId, quantity);
        
        // 检查商品是否存在
        Product product = productService.getProductById(productId);
        if (product == null) {
            throw new BusinessException(404, "商品不存在");
        }
        
        // 检查库存
        if (product.getStock() < quantity) {
            throw new BusinessException(400, "库存不足");
        }
        
        // 检查购物车中是否已存在该商品
        QueryWrapper<CartItem> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId)
                   .eq("product_id", productId);
        CartItem existingItem = cartItemMapper.selectOne(queryWrapper);
        
        if (existingItem != null) {
            // 更新数量
            existingItem.setQuantity(existingItem.getQuantity() + quantity);
            existingItem.setSubtotal(existingItem.getPrice().multiply(BigDecimal.valueOf(existingItem.getQuantity())));
            existingItem.setUpdateTime(LocalDateTime.now());
            cartItemMapper.updateById(existingItem);
            return existingItem;
        } else {
            // 创建新的购物车项
            CartItem cartItem = new CartItem();
            cartItem.setUserId(userId);
            cartItem.setProductId(productId);
            cartItem.setProductName(product.getName());
            cartItem.setProductImage(product.getImage() != null ? product.getImage() : "");
            cartItem.setPrice(product.getPrice());
            cartItem.setQuantity(quantity);
            cartItem.setSubtotal(product.getPrice().multiply(BigDecimal.valueOf(quantity)));
            cartItem.setCreateTime(LocalDateTime.now());
            cartItem.setUpdateTime(LocalDateTime.now());
            
            cartItemMapper.insert(cartItem);
            return cartItem;
        }
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public CartItem updateCartItem(Long userId, Long productId, Integer quantity) {
        log.info("更新购物车商品数量: userId={}, productId={}, quantity={}", userId, productId, quantity);
        
        if (quantity <= 0) {
            throw new BusinessException(400, "数量必须大于0");
        }
        
        // 检查商品库存
        Product product = productService.getProductById(productId);
        if (product.getStock() < quantity) {
            throw new BusinessException(400, "库存不足");
        }
        
        QueryWrapper<CartItem> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId)
                   .eq("product_id", productId);
        CartItem cartItem = cartItemMapper.selectOne(queryWrapper);
        
        if (cartItem == null) {
            throw new BusinessException(404, "购物车中不存在该商品");
        }
        
        cartItem.setQuantity(quantity);
        cartItem.setSubtotal(cartItem.getPrice().multiply(BigDecimal.valueOf(quantity)));
        cartItem.setUpdateTime(LocalDateTime.now());
        cartItemMapper.updateById(cartItem);
        
        return cartItem;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeFromCart(Long userId, Long productId) {
        log.info("从购物车移除商品: userId={}, productId={}", userId, productId);
        
        QueryWrapper<CartItem> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId)
                   .eq("product_id", productId);
        
        cartItemMapper.delete(queryWrapper);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void clearCart(Long userId) {
        log.info("清空购物车: userId={}", userId);
        
        QueryWrapper<CartItem> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        
        cartItemMapper.delete(queryWrapper);
    }
    
    @Override
    public int getCartCount(Long userId) {
        QueryWrapper<CartItem> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        
        Long count = cartItemMapper.selectCount(queryWrapper);
        return count != null ? count.intValue() : 0;
    }
    
    @Override
    public boolean existsInCart(Long userId, Long productId) {
        QueryWrapper<CartItem> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId)
                   .eq("product_id", productId);
        
        return cartItemMapper.selectCount(queryWrapper) > 0;
    }
    
    @Override
    public List<CartItem> checkCartItemsStatus(Long userId) {
        log.info("检查购物车商品状态: userId={}", userId);
        
        List<CartItem> cartItems = getCartItems(userId);
        List<CartItem> updatedItems = new ArrayList<>();
        
        for (CartItem item : cartItems) {
            try {
                Product product = productService.getProductById(item.getProductId());
                
                if (product == null || !Boolean.TRUE.equals(product.getEnabled())) {
                    // 商品已下架
                    item.setStatus("DISABLED");
                } else if (product.getStock() < item.getQuantity()) {
                    // 库存不足
                    item.setStatus("OUT_OF_STOCK");
                } else if (product.getPrice().compareTo(item.getPrice()) != 0) {
                    // 价格变动
                    item.setStatus("PRICE_CHANGED");
                    item.setPrice(product.getPrice());
                    item.setSubtotal(product.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
                } else {
                    // 正常
                    item.setStatus("VALID");
                }
                
                item.setLastCheckedTime(LocalDateTime.now());
                cartItemMapper.updateById(item);
                updatedItems.add(item);
            } catch (Exception e) {
                log.warn("检查商品状态失败: productId={}", item.getProductId(), e);
                item.setStatus("DISABLED");
                cartItemMapper.updateById(item);
                updatedItems.add(item);
            }
        }
        
        return updatedItems;
    }
    
    @Override
    public List<CartItem> getGuestCart(String sessionId) {
        // 从Redis获取游客购物车（简化实现，实际应该使用Redis）
        // 这里返回空列表，实际实现应该从Redis读取
        log.info("获取游客购物车: sessionId={}", sessionId);
        return new ArrayList<>();
    }
    
    @Override
    public void addToGuestCart(String sessionId, Long productId, Integer quantity) {
        // 添加到Redis（简化实现）
        log.info("添加商品到游客购物车: sessionId={}, productId={}, quantity={}", sessionId, productId, quantity);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void mergeGuestCartToUser(String sessionId, Long userId) {
        log.info("合并游客购物车到用户购物车: sessionId={}, userId={}", sessionId, userId);
        
        // 从Redis获取游客购物车数据（简化实现）
        // 实际应该从Redis读取并合并到用户购物车
        List<CartItem> guestCart = getGuestCart(sessionId);
        
        for (CartItem guestItem : guestCart) {
            try {
                addToCart(userId, guestItem.getProductId(), guestItem.getQuantity());
            } catch (Exception e) {
                log.warn("合并购物车项失败: productId={}", guestItem.getProductId(), e);
            }
        }
        
        // 清除游客购物车（从Redis删除）
    }
}

