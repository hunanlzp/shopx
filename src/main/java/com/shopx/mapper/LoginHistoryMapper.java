package com.shopx.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.shopx.entity.LoginHistory;
import org.apache.ibatis.annotations.Mapper;

/**
 * 登录历史Mapper
 */
@Mapper
public interface LoginHistoryMapper extends BaseMapper<LoginHistory> {
}

