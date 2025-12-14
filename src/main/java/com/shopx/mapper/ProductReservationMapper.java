package com.shopx.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.shopx.entity.ProductReservation;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品预订Mapper
 */
@Mapper
public interface ProductReservationMapper extends BaseMapper<ProductReservation> {
}

