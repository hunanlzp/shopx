package com.shopx.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.shopx.entity.CommunityComment;
import org.apache.ibatis.annotations.Mapper;

/**
 * 社区评论Mapper
 */
@Mapper
public interface CommunityCommentMapper extends BaseMapper<CommunityComment> {
}

