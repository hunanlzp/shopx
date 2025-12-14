package com.shopx.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shopx.constant.Constants;
import com.shopx.entity.Product;
import com.shopx.entity.ProductReservation;
import com.shopx.entity.StockNotification;
import com.shopx.exception.BusinessException;
import com.shopx.mapper.ProductMapper;
import com.shopx.mapper.ProductReservationMapper;
import com.shopx.mapper.StockNotificationMapper;
import com.shopx.service.ProductService;
import com.shopx.service.StockService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 库存服务实现类
 */
@Slf4j
@Service
public class StockServiceImpl extends ServiceImpl<StockNotificationMapper, StockNotification> implements StockService {
    
    @Autowired
    private StockNotificationMapper notificationMapper;
    
    @Autowired
    private ProductReservationMapper reservationMapper;
    
    @Autowired
    private ProductMapper productMapper;
    
    @Autowired
    private ProductService productService;
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean checkAndSyncStock(Long productId, Integer quantity) {
        log.info("检查并同步库存: productId={}, quantity={}", productId, quantity);
        
        Product product = productMapper.selectById(productId);
        if (product == null) {
            return false;
        }
        
        // 实时检查库存
        return product.getStock() >= quantity;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public StockNotification addStockNotification(Long userId, Long productId) {
        log.info("添加缺货提醒: userId={}, productId={}", userId, productId);
        
        // 检查商品是否存在
        Product product = productMapper.selectById(productId);
        if (product == null) {
            throw new BusinessException(404, "商品不存在");
        }
        
        // 检查是否已有提醒
        QueryWrapper<StockNotification> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId)
                   .eq("product_id", productId)
                   .eq("status", Constants.StockReservationStatus.PENDING);
        StockNotification existing = notificationMapper.selectOne(queryWrapper);
        if (existing != null) {
            return existing;
        }
        
        // 创建新提醒
        StockNotification notification = new StockNotification();
        notification.setUserId(userId);
        notification.setProductId(productId);
        notification.setStatus(Constants.StockReservationStatus.PENDING);
        
        notificationMapper.insert(notification);
        return notification;
    }
    
    @Override
    public List<StockNotification> getUserStockNotifications(Long userId) {
        QueryWrapper<StockNotification> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId)
                   .orderByDesc("create_time");
        return notificationMapper.selectList(queryWrapper);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean cancelStockNotification(Long notificationId, Long userId) {
        StockNotification notification = notificationMapper.selectById(notificationId);
        if (notification == null || !notification.getUserId().equals(userId)) {
            return false;
        }
        
        notification.setStatus(Constants.StockReservationStatus.CANCELLED);
        notificationMapper.updateById(notification);
        return true;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void notifyStockAvailable(Long productId) {
        log.info("商品到货通知: productId={}", productId);
        
        // 查找所有待通知的提醒
        QueryWrapper<StockNotification> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("product_id", productId)
                   .eq("status", Constants.StockReservationStatus.PENDING);
        List<StockNotification> notifications = notificationMapper.selectList(queryWrapper);
        
        // 发送通知（这里应该调用通知服务）
        for (StockNotification notification : notifications) {
            notification.setStatus("SENT");
            notification.setNotifyTime(LocalDateTime.now());
            notificationMapper.updateById(notification);
            
            // 这里应该发送实际的通知（邮件、短信、推送等）
            log.info("发送到货通知: userId={}, productId={}", notification.getUserId(), productId);
        }
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ProductReservation createReservation(Long userId, Long productId, Integer quantity) {
        log.info("创建商品预订: userId={}, productId={}, quantity={}", userId, productId, quantity);
        
        // 检查商品是否存在
        Product product = productMapper.selectById(productId);
        if (product == null) {
            throw new BusinessException(404, "商品不存在");
        }
        
        // 检查是否已有预订
        QueryWrapper<ProductReservation> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId)
                   .eq("product_id", productId)
                   .eq("status", Constants.StockReservationStatus.PENDING);
        ProductReservation existing = reservationMapper.selectOne(queryWrapper);
        if (existing != null) {
            // 更新数量
            existing.setQuantity(existing.getQuantity() + quantity);
            reservationMapper.updateById(existing);
            return existing;
        }
        
        // 创建新预订
        ProductReservation reservation = new ProductReservation();
        reservation.setUserId(userId);
        reservation.setProductId(productId);
        reservation.setQuantity(quantity);
        reservation.setStatus(Constants.StockReservationStatus.PENDING);
        reservation.setExpectedArrivalTime(LocalDateTime.now().plusDays(7)); // 预计7天后到货
        reservation.setExpireTime(LocalDateTime.now().plusDays(30)); // 30天后过期
        
        reservationMapper.insert(reservation);
        return reservation;
    }
    
    @Override
    public List<ProductReservation> getUserReservations(Long userId) {
        QueryWrapper<ProductReservation> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId)
                   .orderByDesc("create_time");
        return reservationMapper.selectList(queryWrapper);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean cancelReservation(Long reservationId, Long userId) {
        ProductReservation reservation = reservationMapper.selectById(reservationId);
        if (reservation == null || !reservation.getUserId().equals(userId)) {
            return false;
        }
        
        reservation.setStatus(Constants.StockReservationStatus.CANCELLED);
        reservationMapper.updateById(reservation);
        return true;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void fulfillReservation(Long productId) {
        log.info("处理预订: productId={}", productId);
        
        // 查找所有待处理的预订
        QueryWrapper<ProductReservation> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("product_id", productId)
                   .eq("status", Constants.StockReservationStatus.PENDING);
        List<ProductReservation> reservations = reservationMapper.selectList(queryWrapper);
        
        // 检查库存是否足够
        Product product = productMapper.selectById(productId);
        if (product == null || product.getStock() <= 0) {
            return;
        }
        
        // 按创建时间顺序处理预订
        for (ProductReservation reservation : reservations) {
            if (product.getStock() >= reservation.getQuantity()) {
                reservation.setStatus("FULFILLED");
                reservation.setActualArrivalTime(LocalDateTime.now());
                reservationMapper.updateById(reservation);
                
                // 扣减库存
                product.setStock(product.getStock() - reservation.getQuantity());
                productMapper.updateById(product);
                
                // 发送通知（这里应该调用通知服务）
                log.info("预订已满足: reservationId={}, userId={}", reservation.getId(), reservation.getUserId());
            }
        }
    }
    
    @Override
    public List<Product> getAlternativeProducts(Long productId, int limit) {
        log.info("获取替代商品: productId={}, limit={}", productId, limit);
        
        try {
            Product product = productMapper.selectById(productId);
            if (product == null) {
                return List.of();
            }
            
            // 基于类别和价格区间推荐替代商品
            QueryWrapper<Product> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("category", product.getCategory())
                       .ne("id", productId)
                       .gt("stock", 0)
                       .eq("enabled", true)
                       .orderByAsc("price")
                       .last("LIMIT " + limit);
            
            List<Product> alternatives = productMapper.selectList(queryWrapper);
            
            // 如果同类别商品不足，扩展搜索范围
            if (alternatives.size() < limit) {
                QueryWrapper<Product> extendedWrapper = new QueryWrapper<>();
                extendedWrapper.ne("id", productId)
                             .gt("stock", 0)
                             .eq("enabled", true)
                             .orderByDesc("view_count")
                             .last("LIMIT " + (limit - alternatives.size()));
                
                List<Product> extended = productMapper.selectList(extendedWrapper);
                alternatives.addAll(extended);
            }
            
            return alternatives.stream()
                    .limit(limit)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("获取替代商品失败", e);
            return List.of();
        }
    }
}

