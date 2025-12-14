package com.shopx.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.shopx.entity.ABTest;
import org.apache.ibatis.annotations.Mapper;

/**
 * A/B测试Mapper
 */
@Mapper
public interface ABTestMapper extends BaseMapper<ABTest> {
}

