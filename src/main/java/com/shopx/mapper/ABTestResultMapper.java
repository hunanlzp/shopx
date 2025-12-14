package com.shopx.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.shopx.entity.ABTestResult;
import org.apache.ibatis.annotations.Mapper;

/**
 * A/B测试结果Mapper
 */
@Mapper
public interface ABTestResultMapper extends BaseMapper<ABTestResult> {
}

