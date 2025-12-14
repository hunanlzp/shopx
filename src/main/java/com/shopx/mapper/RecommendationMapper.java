package com.shopx.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.shopx.entity.Recommendation;
import org.apache.ibatis.annotations.Mapper;

/**
 * 推荐记录Mapper
 */
@Mapper
public interface RecommendationMapper extends BaseMapper<Recommendation> {
}

