package com.shopx.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.shopx.entity.ReturnOrder;
import org.apache.ibatis.annotations.Mapper;

/**
 * 退货订单Mapper
 */
@Mapper
public interface ReturnOrderMapper extends BaseMapper<ReturnOrder> {
}

