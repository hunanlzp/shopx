package com.shopx.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shopx.entity.*;
import com.shopx.exception.BusinessException;
import com.shopx.mapper.OrderItemMapper;
import com.shopx.mapper.OrderMapper;
import com.shopx.service.CartService;
import com.shopx.service.OrderService;
import com.shopx.service.ProductService;
import com.shopx.util.ResponseUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 订单服务实现类
 */
@Slf4j
@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {
    
    @Autowired
    private OrderMapper orderMapper;
    
    @Autowired
    private OrderItemMapper orderItemMapper;
    
    @Autowired
    private CartService cartService;
    
    @Autowired
    private ProductService productService;
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Order createOrder(Long userId, List<Long> cartItemIds, String shippingAddress) {
        log.info("创建订单: userId={}, cartItemIds={}", userId, cartItemIds);
        
        // 获取购物车商品
        List<CartItem> cartItems = cartService.getCartItems(userId);
        if (cartItems.isEmpty()) {
            throw new BusinessException(400, "购物车为空，无法创建订单");
        }
        
        // 如果指定了cartItemIds，则只选择指定的商品
        if (cartItemIds != null && !cartItemIds.isEmpty()) {
            cartItems = cartItems.stream()
                    .filter(item -> cartItemIds.contains(item.getId()))
                    .collect(Collectors.toList());
        }
        
        if (cartItems.isEmpty()) {
            throw new BusinessException(400, "选择的商品不存在");
        }
        
        // 检查库存并计算总金额
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (CartItem cartItem : cartItems) {
            Product product = productService.getProductById(cartItem.getProductId());
            if (product == null) {
                throw new BusinessException(404, "商品不存在: " + cartItem.getProductId());
            }
            if (product.getStock() < cartItem.getQuantity()) {
                throw new BusinessException(400, "商品库存不足: " + product.getName());
            }
            totalAmount = totalAmount.add(cartItem.getSubtotal());
        }
        
        // 生成订单号
        String orderNo = generateOrderNo();
        
        // 创建订单
        Order order = new Order();
        order.setUserId(userId);
        order.setOrderNo(orderNo);
        order.setTotalAmount(totalAmount);
        order.setStatus("PENDING");
        order.setPaymentStatus("UNPAID");
        order.setShippingAddress(shippingAddress);
        order.setCreateTime(LocalDateTime.now());
        order.setUpdateTime(LocalDateTime.now());
        
        orderMapper.insert(order);
        
        // 创建订单项并扣减库存
        for (CartItem cartItem : cartItems) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrderId(order.getId());
            orderItem.setProductId(cartItem.getProductId());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPrice(cartItem.getPrice());
            orderItem.setSubtotal(cartItem.getSubtotal());
            
            orderItemMapper.insert(orderItem);
            
            // 扣减库存
            Product product = productService.getProductById(cartItem.getProductId());
            product.setStock(product.getStock() - cartItem.getQuantity());
            productService.updateProduct(product.getId(), product);
            
            // 从购物车移除
            cartService.removeFromCart(userId, cartItem.getProductId());
        }
        
        log.info("订单创建成功: orderId={}, orderNo={}", order.getId(), orderNo);
        return order;
    }
    
    @Override
    public ResponseUtil.PageResult<Order> getOrders(Long userId, int page, int size) {
        log.info("获取订单列表: userId={}, page={}, size={}", userId, page, size);
        
        QueryWrapper<Order> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId)
                   .orderByDesc("create_time");
        
        Page<Order> pageParam = new Page<>(page, size);
        Page<Order> result = orderMapper.selectPage(pageParam, queryWrapper);
        
        return ResponseUtil.PageResult.<Order>builder()
                .data(result.getRecords())
                .total(result.getTotal())
                .page(page)
                .size(size)
                .totalPages((int) result.getPages())
                .build();
    }
    
    @Override
    public Order getOrderById(Long orderId) {
        log.info("获取订单详情: orderId={}", orderId);
        
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException(404, "订单不存在");
        }
        
        return order;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean cancelOrder(Long orderId, Long userId) {
        log.info("取消订单: orderId={}, userId={}", orderId, userId);
        
        Order order = getOrderById(orderId);
        
        // 检查权限
        if (!order.getUserId().equals(userId)) {
            throw new BusinessException(403, "无权取消此订单");
        }
        
        // 检查订单状态
        if (!"PENDING".equals(order.getStatus()) && !"PAID".equals(order.getStatus())) {
            throw new BusinessException(400, "订单状态不允许取消");
        }
        
        // 恢复库存
        QueryWrapper<OrderItem> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("order_id", orderId);
        List<OrderItem> orderItems = orderItemMapper.selectList(queryWrapper);
        
        for (OrderItem orderItem : orderItems) {
            Product product = productService.getProductById(orderItem.getProductId());
            product.setStock(product.getStock() + orderItem.getQuantity());
            productService.updateProduct(product.getId(), product);
        }
        
        // 更新订单状态
        order.setStatus("CANCELLED");
        order.setUpdateTime(LocalDateTime.now());
        orderMapper.updateById(order);
        
        log.info("订单取消成功: orderId={}", orderId);
        return true;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateOrderStatus(Long orderId, String status) {
        log.info("更新订单状态: orderId={}, status={}", orderId, status);
        
        Order order = getOrderById(orderId);
        order.setStatus(status);
        order.setUpdateTime(LocalDateTime.now());
        orderMapper.updateById(order);
        
        return true;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean payOrder(Long orderId, String paymentMethod) {
        log.info("支付订单: orderId={}, paymentMethod={}", orderId, paymentMethod);
        
        Order order = getOrderById(orderId);
        
        if (!"PENDING".equals(order.getStatus())) {
            throw new BusinessException(400, "订单状态不允许支付");
        }
        
        // 这里应该调用支付接口，目前模拟支付成功
        order.setStatus("PAID");
        order.setPaymentStatus("PAID");
        order.setUpdateTime(LocalDateTime.now());
        orderMapper.updateById(order);
        
        log.info("订单支付成功: orderId={}", orderId);
        return true;
    }
    
    /**
     * 生成订单号
     */
    private String generateOrderNo() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String uuid = UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();
        return "ORD" + timestamp + uuid;
    }
}

