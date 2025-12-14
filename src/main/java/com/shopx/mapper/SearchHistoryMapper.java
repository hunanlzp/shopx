package com.shopx.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.shopx.entity.SearchHistory;
import org.apache.ibatis.annotations.Mapper;

/**
 * 搜索历史Mapper
 */
@Mapper
public interface SearchHistoryMapper extends BaseMapper<SearchHistory> {
}

