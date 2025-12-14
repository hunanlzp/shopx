package com.shopx.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.shopx.entity.Wishlist;
import org.apache.ibatis.annotations.Mapper;

/**
 * 愿望清单Mapper
 */
@Mapper
public interface WishlistMapper extends BaseMapper<Wishlist> {
}

