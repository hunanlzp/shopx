package com.shopx.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.shopx.entity.CustomerServiceTicket;
import org.apache.ibatis.annotations.Mapper;

/**
 * 客服工单Mapper
 */
@Mapper
public interface CustomerServiceTicketMapper extends BaseMapper<CustomerServiceTicket> {
}

