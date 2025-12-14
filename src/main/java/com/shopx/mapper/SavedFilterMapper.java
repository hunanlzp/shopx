package com.shopx.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.shopx.entity.SavedFilter;
import org.apache.ibatis.annotations.Mapper;

/**
 * 保存的筛选条件Mapper
 */
@Mapper
public interface SavedFilterMapper extends BaseMapper<SavedFilter> {
}

