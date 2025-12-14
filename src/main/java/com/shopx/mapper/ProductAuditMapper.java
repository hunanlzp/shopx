package com.shopx.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.shopx.entity.ProductAudit;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品审核Mapper
 */
@Mapper
public interface ProductAuditMapper extends BaseMapper<ProductAudit> {
}

