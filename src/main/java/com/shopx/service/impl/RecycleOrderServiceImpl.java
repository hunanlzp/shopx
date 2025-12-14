package com.shopx.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shopx.entity.RecycleOrder;
import com.shopx.mapper.RecycleOrderMapper;
import com.shopx.service.RecycleOrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 回收订单服务实现类
 */
@Slf4j
@Service
public class RecycleOrderServiceImpl extends ServiceImpl<RecycleOrderMapper, RecycleOrder> implements RecycleOrderService {

    @Override
    public List<RecycleOrder> getByUserId(Long userId) {
        QueryWrapper<RecycleOrder> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        queryWrapper.orderByDesc("create_time");
        return list(queryWrapper);
    }

    @Override
    public List<RecycleOrder> getByStatus(String status) {
        QueryWrapper<RecycleOrder> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("status", status);
        queryWrapper.orderByDesc("create_time");
        return list(queryWrapper);
    }

    @Override
    public boolean updateOrderStatus(Long orderId, String status) {
        RecycleOrder order = getById(orderId);
        if (order == null) {
            return false;
        }

        order.setStatus(status);
        order.setUpdateTime(LocalDateTime.now());
        
        if ("COMPLETED".equals(status)) {
            order.setCompletionDate(LocalDateTime.now());
        }

        return updateById(order);
    }

    @Override
    public Map<String, Object> getUserRecycleStats(Long userId) {
        List<RecycleOrder> orders = getByUserId(userId);
        
        Map<String, Object> stats = new HashMap<>();
        
        int totalOrders = orders.size();
        double totalValue = orders.stream()
                .filter(order -> order.getActualValue() != null)
                .mapToDouble(RecycleOrder::getActualValue)
                .sum();
        
        int completedOrders = (int) orders.stream()
                .filter(order -> "COMPLETED".equals(order.getStatus()))
                .count();
        
        int sustainabilityScore = completedOrders * 10;
        
        stats.put("totalOrders", totalOrders);
        stats.put("totalValue", totalValue);
        stats.put("completedOrders", completedOrders);
        stats.put("sustainabilityScore", sustainabilityScore);
        stats.put("carbonSaved", completedOrders * 2.5);
        stats.put("treesPlanted", completedOrders * 0.1);
        stats.put("waterSaved", completedOrders * 50.0);
        stats.put("energySaved", completedOrders * 5.0);
        
        return stats;
    }
}
