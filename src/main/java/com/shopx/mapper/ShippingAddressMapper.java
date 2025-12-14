package com.shopx.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.shopx.entity.ShippingAddress;
import org.apache.ibatis.annotations.Mapper;

/**
 * 收货地址Mapper
 */
@Mapper
public interface ShippingAddressMapper extends BaseMapper<ShippingAddress> {
}

