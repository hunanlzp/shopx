package com.shopx.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.shopx.entity.FAQ;
import org.apache.ibatis.annotations.Mapper;

/**
 * 常见问题Mapper
 */
@Mapper
public interface FAQMapper extends BaseMapper<FAQ> {
}

