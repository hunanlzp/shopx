package com.shopx.service;

import com.shopx.entity.*;

import java.util.List;

/**
 * 价值循环服务接口
 */
public interface RecycleService {
    
    /**
     * 创建回收订单
     * @param recycleOrder 回收订单
     * @return 创建的回收订单
     */
    RecycleOrder createRecycleOrder(RecycleOrder recycleOrder);
    
    /**
     * 获取用户回收订单列表
     * @param userId 用户ID
     * @param status 订单状态
     * @return 回收订单列表
     */
    List<RecycleOrder> getUserRecycleOrders(Long userId, String status);
    
    /**
     * 根据ID获取回收订单
     * @param id 订单ID
     * @return 回收订单
     */
    RecycleOrder getRecycleOrderById(Long id);
    
    /**
     * 更新回收订单状态
     * @param id 订单ID
     * @param status 新状态
     * @return 更新后的订单
     */
    RecycleOrder updateRecycleOrderStatus(Long id, String status);
    
    /**
     * 获取用户回收统计
     * @param userId 用户ID
     * @return 回收统计
     */
    RecycleStats getUserRecycleStats(Long userId);
    
    /**
     * 获取环保活动列表
     * @param status 活动状态
     * @return 环保活动列表
     */
    List<EcoActivity> getEcoActivities(String status);
    
    /**
     * 参与环保活动
     * @param activityId 活动ID
     * @param userId 用户ID
     * @return 参与记录
     */
    UserEcoRecord joinEcoActivity(Long activityId, Long userId);
    
    /**
     * 获取价值循环页面数据
     * @param userId 用户ID
     * @return 页面数据
     */
    RecyclePageData getRecyclePageData(Long userId);
    
    /**
     * 获取环保积分排行榜
     * @param limit 排行榜数量
     * @return 排行榜
     */
    List<UserEcoRanking> getEcoRanking(Integer limit);
}
