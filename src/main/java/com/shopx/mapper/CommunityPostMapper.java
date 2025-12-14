package com.shopx.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.shopx.entity.CommunityPost;
import org.apache.ibatis.annotations.Mapper;

/**
 * 社区帖子Mapper
 */
@Mapper
public interface CommunityPostMapper extends BaseMapper<CommunityPost> {
}

