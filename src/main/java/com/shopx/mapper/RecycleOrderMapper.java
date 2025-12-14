package com.shopx.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.shopx.entity.RecycleOrder;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 回收订单Mapper接口
 */
@Mapper
public interface RecycleOrderMapper extends BaseMapper<RecycleOrder> {

    /**
     * 根据用户ID获取回收订单列表
     * @param userId 用户ID
     * @return 回收订单列表
     */
    @Select("SELECT * FROM t_recycle_order WHERE user_id = #{userId} ORDER BY create_time DESC")
    List<RecycleOrder> selectByUserId(Long userId);

    /**
     * 根据状态获取回收订单列表
     * @param status 订单状态
     * @return 回收订单列表
     */
    @Select("SELECT * FROM t_recycle_order WHERE status = #{status} ORDER BY create_time DESC")
    List<RecycleOrder> selectByStatus(String status);

    /**
     * 获取用户回收统计
     * @param userId 用户ID
     * @return 统计信息
     */
    @Select("SELECT " +
            "COUNT(*) as totalOrders, " +
            "SUM(CASE WHEN actual_value IS NOT NULL THEN actual_value ELSE 0 END) as totalValue, " +
            "SUM(CASE WHEN status = 'COMPLETED' THEN 1 ELSE 0 END) as completedOrders " +
            "FROM t_recycle_order WHERE user_id = #{userId}")
    Map<String, Object> selectUserStats(Long userId);
}