package com.shopx.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.shopx.entity.UserRecommendationPreference;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户推荐偏好Mapper
 */
@Mapper
public interface UserRecommendationPreferenceMapper extends BaseMapper<UserRecommendationPreference> {
}

