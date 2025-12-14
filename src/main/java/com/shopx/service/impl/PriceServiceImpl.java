package com.shopx.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shopx.constant.Constants;
import com.shopx.dto.PriceCalculationDTO;
import com.shopx.entity.PriceHistory;
import com.shopx.entity.PriceProtection;
import com.shopx.entity.Product;
import com.shopx.mapper.PriceHistoryMapper;
import com.shopx.mapper.PriceProtectionMapper;
import com.shopx.mapper.ProductMapper;
import com.shopx.service.PriceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 价格服务实现类
 */
@Slf4j
@Service
public class PriceServiceImpl extends ServiceImpl<PriceHistoryMapper, PriceHistory> implements PriceService {
    
    @Autowired
    private PriceHistoryMapper priceHistoryMapper;
    
    @Autowired
    private PriceProtectionMapper priceProtectionMapper;
    
    @Autowired
    private ProductMapper productMapper;
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void recordPriceChange(Product product, String reason) {
        PriceHistory history = new PriceHistory();
        history.setProductId(product.getId());
        history.setPrice(product.getPrice());
        history.setShippingFee(product.getShippingFee() != null ? product.getShippingFee() : BigDecimal.ZERO);
        
        // 计算税费（默认税率为10%）
        BigDecimal taxRate = product.getTaxRate() != null ? product.getTaxRate() : new BigDecimal("10.00");
        BigDecimal tax = product.getPrice().multiply(taxRate).divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
        history.setTax(tax);
        
        // 计算总价
        BigDecimal totalPrice = product.getPrice()
                .add(history.getShippingFee())
                .add(tax);
        history.setTotalPrice(totalPrice);
        history.setReason(reason);
        
        priceHistoryMapper.insert(history);
    }
    
    @Override
    public List<PriceHistory> getPriceHistory(Long productId, int days) {
        LocalDateTime startDate = LocalDateTime.now().minusDays(days);
        
        QueryWrapper<PriceHistory> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("product_id", productId)
                   .ge("create_time", startDate)
                   .orderByDesc("create_time");
        
        return priceHistoryMapper.selectList(queryWrapper);
    }
    
    @Override
    public PriceCalculationDTO calculateTotalPrice(Long productId, Integer quantity, Long shippingAddressId) {
        Product product = productMapper.selectById(productId);
        if (product == null) {
            throw new RuntimeException("商品不存在");
        }
        
        BigDecimal productPrice = product.getPrice();
        BigDecimal shippingFee = product.getShippingFee() != null ? product.getShippingFee() : BigDecimal.ZERO;
        
        // 根据地址计算运费（简化实现，实际应该根据地址查询）
        // TODO: 根据shippingAddressId查询地址并计算运费
        
        // 计算税费
        BigDecimal taxRate = product.getTaxRate() != null ? product.getTaxRate() : new BigDecimal("10.00");
        BigDecimal subtotal = productPrice.multiply(BigDecimal.valueOf(quantity));
        BigDecimal taxAmount = subtotal.multiply(taxRate).divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
        
        // 计算总价
        BigDecimal total = subtotal.add(shippingFee).add(taxAmount);
        
        PriceCalculationDTO result = new PriceCalculationDTO();
        result.setProductPrice(productPrice);
        result.setQuantity(quantity);
        result.setSubtotal(subtotal);
        result.setShippingFee(shippingFee);
        result.setTaxRate(taxRate.divide(new BigDecimal("100"), 4, RoundingMode.HALF_UP));
        result.setTaxAmount(taxAmount);
        result.setTotal(total);
        
        return result;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public PriceProtection createPriceProtection(Long orderId, Long userId, Long productId, BigDecimal purchasePrice) {
        PriceProtection protection = new PriceProtection();
        protection.setOrderId(orderId);
        protection.setUserId(userId);
        protection.setProductId(productId);
        protection.setPurchasePrice(purchasePrice);
        protection.setCurrentPrice(purchasePrice);
        protection.setPriceDifference(BigDecimal.ZERO);
        protection.setStatus(Constants.PriceProtectionStatus.PENDING);
        protection.setStartTime(LocalDateTime.now());
        protection.setEndTime(LocalDateTime.now().plusDays(7));
        
        priceProtectionMapper.insert(protection);
        return protection;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<PriceProtection> checkAndRefundPriceProtection() {
        QueryWrapper<PriceProtection> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("status", Constants.PriceProtectionStatus.PENDING)
                   .le("end_time", LocalDateTime.now());
        
        List<PriceProtection> protections = priceProtectionMapper.selectList(queryWrapper);
        
        for (PriceProtection protection : protections) {
            Product product = productMapper.selectById(protection.getProductId());
            if (product != null) {
                BigDecimal currentPrice = product.getPrice();
                protection.setCurrentPrice(currentPrice);
                
                if (currentPrice.compareTo(protection.getPurchasePrice()) < 0) {
                    // 价格下降，计算差价
                    BigDecimal difference = protection.getPurchasePrice().subtract(currentPrice);
                    protection.setPriceDifference(difference);
                    protection.setStatus(Constants.PriceProtectionStatus.COMPLETED);
                    
                    // 这里应该调用退款服务，简化实现
                    log.info("价格保护退款: orderId={}, difference={}", protection.getOrderId(), difference);
                } else {
                    protection.setStatus(Constants.PriceProtectionStatus.REJECTED);
                }
                
                priceProtectionMapper.updateById(protection);
            }
        }
        
        return protections;
    }
    
    @Override
    public List<PriceProtection> getUserPriceProtections(Long userId) {
        QueryWrapper<PriceProtection> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId)
                   .orderByDesc("create_time");
        return priceProtectionMapper.selectList(queryWrapper);
    }
}

