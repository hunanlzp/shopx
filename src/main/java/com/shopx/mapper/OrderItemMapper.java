package com.shopx.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.shopx.entity.OrderItem;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单项Mapper接口
 */
@Mapper
public interface OrderItemMapper extends BaseMapper<OrderItem> {
}

