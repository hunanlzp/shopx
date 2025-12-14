package com.shopx.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shopx.entity.Order;
import com.shopx.entity.OrderItem;
import com.shopx.entity.ReturnOrder;
import com.shopx.exception.BusinessException;
import com.shopx.mapper.OrderItemMapper;
import com.shopx.mapper.OrderMapper;
import com.shopx.mapper.ReturnOrderMapper;
import com.shopx.service.ReturnService;
import com.shopx.util.ResponseUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * 退货服务实现类
 */
@Slf4j
@Service
public class ReturnServiceImpl extends ServiceImpl<ReturnOrderMapper, ReturnOrder> implements ReturnService {
    
    @Autowired
    private ReturnOrderMapper returnOrderMapper;
    
    @Autowired
    private OrderMapper orderMapper;
    
    @Autowired
    private OrderItemMapper orderItemMapper;
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ReturnOrder createReturnOrder(Long orderId, Long productId, Integer quantity, String reason, String description) {
        log.info("创建退货订单: orderId={}, productId={}, quantity={}", orderId, productId, quantity);
        
        // 检查订单是否存在
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException(404, "订单不存在");
        }
        
        // 检查订单状态（只有已收货的订单才能退货）
        if (!"DELIVERED".equals(order.getStatus())) {
            throw new BusinessException(400, "只有已收货的订单才能申请退货");
        }
        
        // 检查订单项
        QueryWrapper<OrderItem> itemWrapper = new QueryWrapper<>();
        itemWrapper.eq("order_id", orderId)
                   .eq("product_id", productId);
        OrderItem orderItem = orderItemMapper.selectOne(itemWrapper);
        if (orderItem == null) {
            throw new BusinessException(404, "订单中不存在该商品");
        }
        
        if (quantity > orderItem.getQuantity()) {
            throw new BusinessException(400, "退货数量不能超过购买数量");
        }
        
        // 创建退货订单
        ReturnOrder returnOrder = new ReturnOrder();
        returnOrder.setOrderId(orderId);
        returnOrder.setUserId(order.getUserId());
        returnOrder.setProductId(productId);
        returnOrder.setQuantity(quantity);
        returnOrder.setReason(reason);
        returnOrder.setDescription(description);
        returnOrder.setRefundAmount(orderItem.getPrice().multiply(BigDecimal.valueOf(quantity)));
        returnOrder.setStatus("PENDING");
        
        returnOrderMapper.insert(returnOrder);
        
        // 更新订单退货状态
        order.setReturnStatus("PENDING");
        orderMapper.updateById(order);
        
        return returnOrder;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ReturnOrder auditReturnOrder(Long returnOrderId, String status, String comment) {
        ReturnOrder returnOrder = returnOrderMapper.selectById(returnOrderId);
        if (returnOrder == null) {
            throw new BusinessException(404, "退货订单不存在");
        }
        
        returnOrder.setStatus(status);
        returnOrder.setAuditComment(comment);
        returnOrder.setAuditTime(LocalDateTime.now());
        
        if ("APPROVED".equals(status)) {
            // 生成退货标签
            String label = generateReturnLabel(returnOrderId);
            returnOrder.setReturnLabel(label);
        }
        
        returnOrderMapper.updateById(returnOrder);
        return returnOrder;
    }
    
    @Override
    public String generateReturnLabel(Long returnOrderId) {
        ReturnOrder returnOrder = returnOrderMapper.selectById(returnOrderId);
        if (returnOrder == null) {
            return null;
        }
        
        // 生成退货标签（可以是打印标签或电子标签URL）
        String labelId = UUID.randomUUID().toString();
        String labelUrl = "/return-labels/" + labelId + ".pdf";
        
        returnOrder.setReturnLabel(labelUrl);
        returnOrderMapper.updateById(returnOrder);
        
        return labelUrl;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean processRefund(Long returnOrderId) {
        ReturnOrder returnOrder = returnOrderMapper.selectById(returnOrderId);
        if (returnOrder == null) {
            return false;
        }
        
        if (!"APPROVED".equals(returnOrder.getStatus())) {
            throw new BusinessException(400, "只有审核通过的退货订单才能退款");
        }
        
        // 这里应该调用支付系统的退款API
        // 简化实现：直接标记为已退款
        returnOrder.setStatus("COMPLETED");
        returnOrder.setRefundTime(LocalDateTime.now());
        returnOrderMapper.updateById(returnOrder);
        
        // 更新订单状态
        Order order = orderMapper.selectById(returnOrder.getOrderId());
        if (order != null) {
            order.setReturnStatus("COMPLETED");
            orderMapper.updateById(order);
        }
        
        log.info("退款处理完成: returnOrderId={}, refundAmount={}", returnOrderId, returnOrder.getRefundAmount());
        return true;
    }
    
    @Override
    public ResponseUtil.PageResult<ReturnOrder> getUserReturnOrders(Long userId, int page, int size) {
        QueryWrapper<ReturnOrder> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId)
                   .orderByDesc("create_time");
        
        Page<ReturnOrder> pageParam = new Page<>(page, size);
        Page<ReturnOrder> result = returnOrderMapper.selectPage(pageParam, queryWrapper);
        
        return ResponseUtil.PageResult.<ReturnOrder>builder()
                .data(result.getRecords())
                .total(result.getTotal())
                .page(page)
                .size(size)
                .totalPages((int) result.getPages())
                .build();
    }
    
    @Override
    public ReturnOrder getReturnOrderById(Long returnOrderId) {
        return returnOrderMapper.selectById(returnOrderId);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean cancelReturnOrder(Long returnOrderId, Long userId) {
        ReturnOrder returnOrder = returnOrderMapper.selectById(returnOrderId);
        if (returnOrder == null || !returnOrder.getUserId().equals(userId)) {
            return false;
        }
        
        if (!"PENDING".equals(returnOrder.getStatus())) {
            throw new BusinessException(400, "只能取消待审核的退货申请");
        }
        
        returnOrder.setStatus("CANCELLED");
        returnOrderMapper.updateById(returnOrder);
        
        // 更新订单退货状态
        Order order = orderMapper.selectById(returnOrder.getOrderId());
        if (order != null) {
            order.setReturnStatus("NONE");
            orderMapper.updateById(order);
        }
        
        return true;
    }
}

