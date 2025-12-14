package com.shopx.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.shopx.entity.StockNotification;
import org.apache.ibatis.annotations.Mapper;

/**
 * 库存提醒Mapper
 */
@Mapper
public interface StockNotificationMapper extends BaseMapper<StockNotification> {
}

