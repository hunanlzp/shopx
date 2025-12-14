package com.shopx.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.shopx.entity.RecycleOrder;

import java.util.List;

/**
 * 回收订单服务接口
 */
public interface RecycleOrderService extends IService<RecycleOrder> {

    /**
     * 根据用户ID获取回收订单列表
     * @param userId 用户ID
     * @return 回收订单列表
     */
    List<RecycleOrder> getByUserId(Long userId);

    /**
     * 根据状态获取回收订单列表
     * @param status 订单状态
     * @return 回收订单列表
     */
    List<RecycleOrder> getByStatus(String status);

    /**
     * 更新订单状态
     * @param orderId 订单ID
     * @param status 新状态
     * @return 更新结果
     */
    boolean updateOrderStatus(Long orderId, String status);

    /**
     * 获取用户回收统计
     * @param userId 用户ID
     * @return 统计信息
     */
    Map<String, Object> getUserRecycleStats(Long userId);
}
