package com.shopx.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.shopx.entity.ProductReview;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品评价Mapper
 */
@Mapper
public interface ProductReviewMapper extends BaseMapper<ProductReview> {
}

