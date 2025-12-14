package com.shopx.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.shopx.entity.PriceHistory;
import org.apache.ibatis.annotations.Mapper;

/**
 * 价格历史Mapper
 */
@Mapper
public interface PriceHistoryMapper extends BaseMapper<PriceHistory> {
}

