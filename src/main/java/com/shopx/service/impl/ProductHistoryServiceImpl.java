package com.shopx.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shopx.entity.ProductHistory;
import com.shopx.mapper.ProductHistoryMapper;
import com.shopx.service.ProductHistoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 商品历史服务实现类
 */
@Slf4j
@Service
public class ProductHistoryServiceImpl extends ServiceImpl<ProductHistoryMapper, ProductHistory> implements ProductHistoryService {
    
    @Autowired
    private ProductHistoryMapper historyMapper;
    
    @Override
    public void recordProductChange(Long productId, Long modifiedBy, String changes, String reason) {
        ProductHistory history = new ProductHistory();
        history.setProductId(productId);
        history.setModifiedBy(modifiedBy);
        history.setChanges(changes);
        history.setReason(reason);
        
        historyMapper.insert(history);
    }
    
    @Override
    public List<ProductHistory> getProductHistory(Long productId, int limit) {
        QueryWrapper<ProductHistory> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("product_id", productId)
                   .orderByDesc("create_time")
                   .last("LIMIT " + limit);
        return historyMapper.selectList(queryWrapper);
    }
}

