package com.shopx.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.shopx.entity.ShoppingSession;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * 购物会话Mapper
 */
@Mapper
public interface ShoppingSessionMapper extends BaseMapper<ShoppingSession> {
    
    @Select("SELECT * FROM t_shopping_session WHERE session_id = #{sessionId}")
    ShoppingSession selectBySessionId(String sessionId);
}

