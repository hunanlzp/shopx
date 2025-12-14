package com.shopx.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.shopx.entity.ProductHistory;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品历史Mapper
 */
@Mapper
public interface ProductHistoryMapper extends BaseMapper<ProductHistory> {
}

