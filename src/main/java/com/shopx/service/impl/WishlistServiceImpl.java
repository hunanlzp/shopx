package com.shopx.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shopx.entity.Product;
import com.shopx.entity.Wishlist;
import com.shopx.mapper.ProductMapper;
import com.shopx.mapper.WishlistMapper;
import com.shopx.service.WishlistService;
import com.shopx.util.ResponseUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 愿望清单服务实现类
 */
@Slf4j
@Service
public class WishlistServiceImpl extends ServiceImpl<WishlistMapper, Wishlist> implements WishlistService {
    
    @Autowired
    private WishlistMapper wishlistMapper;
    
    @Autowired
    private ProductMapper productMapper;
    
    // 分享链接缓存（实际应该使用Redis）
    private final Map<String, List<Long>> shareLinkCache = new HashMap<>();
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Wishlist addToWishlist(Long userId, Long productId, String category, String notes) {
        log.info("添加到愿望清单: userId={}, productId={}, category={}", userId, productId, category);
        
        // 检查商品是否存在
        Product product = productMapper.selectById(productId);
        if (product == null) {
            throw new RuntimeException("商品不存在");
        }
        
        // 检查是否已在愿望清单中
        QueryWrapper<Wishlist> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId)
                   .eq("product_id", productId);
        Wishlist existing = wishlistMapper.selectOne(queryWrapper);
        
        if (existing != null) {
            // 更新分类和备注
            if (StringUtils.hasText(category)) {
                existing.setCategory(category);
            }
            if (StringUtils.hasText(notes)) {
                existing.setNotes(notes);
            }
            wishlistMapper.updateById(existing);
            return existing;
        }
        
        // 创建新记录
        Wishlist wishlist = new Wishlist();
        wishlist.setUserId(userId);
        wishlist.setProductId(productId);
        wishlist.setCategory(category);
        wishlist.setNotes(notes);
        wishlist.setPriceAlert(false);
        
        wishlistMapper.insert(wishlist);
        return wishlist;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean removeFromWishlist(Long userId, Long productId) {
        QueryWrapper<Wishlist> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId)
                   .eq("product_id", productId);
        
        return wishlistMapper.delete(queryWrapper) > 0;
    }
    
    @Override
    public ResponseUtil.PageResult<Wishlist> getUserWishlist(Long userId, String category, int page, int size) {
        QueryWrapper<Wishlist> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        
        if (StringUtils.hasText(category)) {
            queryWrapper.eq("category", category);
        }
        
        queryWrapper.orderByDesc("create_time");
        
        Page<Wishlist> pageParam = new Page<>(page, size);
        Page<Wishlist> result = wishlistMapper.selectPage(pageParam, queryWrapper);
        
        return ResponseUtil.PageResult.<Wishlist>builder()
                .data(result.getRecords())
                .total(result.getTotal())
                .page(page)
                .size(size)
                .totalPages((int) result.getPages())
                .build();
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean setPriceAlert(Long userId, Long productId, BigDecimal targetPrice) {
        QueryWrapper<Wishlist> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId)
                   .eq("product_id", productId);
        
        Wishlist wishlist = wishlistMapper.selectOne(queryWrapper);
        if (wishlist == null) {
            return false;
        }
        
        wishlist.setPriceAlert(true);
        wishlist.setTargetPrice(targetPrice);
        wishlistMapper.updateById(wishlist);
        
        return true;
    }
    
    @Override
    public List<Wishlist> checkPriceDrops() {
        log.info("检查价格下降");
        
        // 获取所有设置了价格提醒的愿望清单
        QueryWrapper<Wishlist> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("price_alert", true);
        List<Wishlist> wishlists = wishlistMapper.selectList(queryWrapper);
        
        List<Wishlist> priceDropped = new ArrayList<>();
        
        for (Wishlist wishlist : wishlists) {
            Product product = productMapper.selectById(wishlist.getProductId());
            if (product != null && wishlist.getTargetPrice() != null) {
                // 检查价格是否下降到目标价格以下
                if (product.getPrice().compareTo(wishlist.getTargetPrice()) <= 0) {
                    priceDropped.add(wishlist);
                    
                    // 这里应该发送通知（邮件、短信、推送等）
                    log.info("价格下降提醒: userId={}, productId={}, currentPrice={}, targetPrice={}",
                            wishlist.getUserId(), wishlist.getProductId(),
                            product.getPrice(), wishlist.getTargetPrice());
                }
            }
        }
        
        return priceDropped;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean batchOperation(Long userId, List<Long> wishlistIds, String operation, String targetCategory) {
        if (wishlistIds == null || wishlistIds.isEmpty()) {
            return false;
        }
        
        QueryWrapper<Wishlist> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId)
                   .in("id", wishlistIds);
        
        List<Wishlist> wishlists = wishlistMapper.selectList(queryWrapper);
        
        if (wishlists.isEmpty()) {
            return false;
        }
        
        switch (operation.toUpperCase()) {
            case "DELETE":
                // 批量删除
                for (Wishlist wishlist : wishlists) {
                    wishlistMapper.deleteById(wishlist.getId());
                }
                break;
                
            case "MOVE":
                // 移动到分类
                if (StringUtils.hasText(targetCategory)) {
                    for (Wishlist wishlist : wishlists) {
                        wishlist.setCategory(targetCategory);
                        wishlistMapper.updateById(wishlist);
                    }
                }
                break;
                
            default:
                return false;
        }
        
        return true;
    }
    
    @Override
    public String shareWishlist(Long userId, String category) {
        // 获取愿望清单商品ID列表
        QueryWrapper<Wishlist> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        
        if (StringUtils.hasText(category)) {
            queryWrapper.eq("category", category);
        }
        
        List<Wishlist> wishlists = wishlistMapper.selectList(queryWrapper);
        List<Long> productIds = wishlists.stream()
                .map(Wishlist::getProductId)
                .collect(Collectors.toList());
        
        // 生成分享链接
        String shareLink = UUID.randomUUID().toString();
        shareLinkCache.put(shareLink, productIds);
        
        return shareLink;
    }
    
    @Override
    public List<Product> getWishlistByShareLink(String shareLink) {
        List<Long> productIds = shareLinkCache.get(shareLink);
        if (productIds == null || productIds.isEmpty()) {
            return new ArrayList<>();
        }
        
        return productMapper.selectBatchIds(productIds);
    }
}

