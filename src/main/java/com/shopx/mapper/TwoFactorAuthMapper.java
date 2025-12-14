package com.shopx.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.shopx.entity.TwoFactorAuth;
import org.apache.ibatis.annotations.Mapper;

/**
 * 双因素认证Mapper
 */
@Mapper
public interface TwoFactorAuthMapper extends BaseMapper<TwoFactorAuth> {
}

