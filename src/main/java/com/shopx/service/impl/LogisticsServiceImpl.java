package com.shopx.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shopx.constant.Constants;
import com.shopx.entity.LogisticsTracking;
import com.shopx.entity.ShippingAddress;
import com.shopx.mapper.LogisticsTrackingMapper;
import com.shopx.mapper.ShippingAddressMapper;
import com.shopx.service.LogisticsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 物流服务实现类
 */
@Slf4j
@Service
public class LogisticsServiceImpl extends ServiceImpl<LogisticsTrackingMapper, LogisticsTracking> implements LogisticsService {
    
    @Autowired
    private LogisticsTrackingMapper trackingMapper;
    
    @Autowired
    private ShippingAddressMapper addressMapper;
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public LogisticsTracking createTracking(Long orderId, String trackingNumber, String logisticsCompany, String shippingMethod) {
        LogisticsTracking tracking = new LogisticsTracking();
        tracking.setOrderId(orderId);
        tracking.setTrackingNumber(trackingNumber);
        tracking.setLogisticsCompany(logisticsCompany);
        tracking.setStatus(Constants.LogisticsStatus.PENDING);
        
        // 根据配送方式设置预计送达时间
        LocalDateTime estimatedTime = estimateDeliveryTime(shippingMethod, null);
        tracking.setEstimatedDeliveryTime(estimatedTime);
        
        trackingMapper.insert(tracking);
        return tracking;
    }
    
    @Override
    public LogisticsTracking syncLogisticsInfo(String trackingNumber, String logisticsCompany) {
        QueryWrapper<LogisticsTracking> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("tracking_number", trackingNumber);
        LogisticsTracking tracking = trackingMapper.selectOne(queryWrapper);
        
        if (tracking == null) {
            return null;
        }
        
        // 这里应该调用第三方物流API（如菜鸟、顺丰等）
        // 简化实现：模拟物流状态更新
        String[] statuses = {
            Constants.LogisticsStatus.PENDING,
            Constants.LogisticsStatus.IN_TRANSIT,
            Constants.LogisticsStatus.OUT_FOR_DELIVERY,
            Constants.LogisticsStatus.DELIVERED
        };
        int currentIndex = getStatusIndex(tracking.getStatus());
        if (currentIndex < statuses.length - 1) {
            tracking.setStatus(statuses[currentIndex + 1]);
        }
        
        // 模拟位置更新
        tracking.setCurrentLocation("配送中");
        
        // 更新详细信息
        Map<String, Object> details = new HashMap<>();
        details.put("lastUpdate", LocalDateTime.now().toString());
        details.put("status", tracking.getStatus());
        tracking.setDetails(convertToJson(details));
        
        trackingMapper.updateById(tracking);
        return tracking;
    }
    
    @Override
    public LogisticsTracking getTrackingInfo(Long orderId) {
        QueryWrapper<LogisticsTracking> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("order_id", orderId)
                   .orderByDesc("create_time")
                   .last("LIMIT 1");
        return trackingMapper.selectOne(queryWrapper);
    }
    
    @Override
    public BigDecimal calculateShippingFee(String shippingMethod, String shippingAddress, BigDecimal totalAmount) {
        BigDecimal fee = BigDecimal.ZERO;
        
        // 根据配送方式计算运费
        switch (shippingMethod) {
            case "STANDARD":
                fee = new BigDecimal("10.00");
                // 偏远地区加收运费
                if (shippingAddress != null && shippingAddress.contains("偏远")) {
                    fee = fee.add(new BigDecimal("5.00"));
                }
                break;
            case "EXPRESS":
                fee = new BigDecimal("20.00");
                break;
            case "PICKUP":
                fee = BigDecimal.ZERO; // 自提免费
                break;
            default:
                fee = new BigDecimal("10.00");
        }
        
        // 满99免运费
        if (totalAmount.compareTo(new BigDecimal("99.00")) >= 0 && !"EXPRESS".equals(shippingMethod)) {
            fee = BigDecimal.ZERO;
        }
        
        return fee;
    }
    
    @Override
    public LocalDateTime estimateDeliveryTime(String shippingMethod, String shippingAddress) {
        LocalDateTime now = LocalDateTime.now();
        
        switch (shippingMethod) {
            case "STANDARD":
                // 标准配送：3-5天
                return now.plusDays(4);
            case "EXPRESS":
                // 加急配送：1-2天
                return now.plusDays(1);
            case "PICKUP":
                // 自提：当天
                return now.plusHours(2);
            default:
                return now.plusDays(3);
        }
    }
    
    @Override
    public List<com.shopx.dto.ShippingOptionDTO> getShippingOptions(String shippingAddress) {
        List<com.shopx.dto.ShippingOptionDTO> options = new ArrayList<>();
        
        // 标准配送
        com.shopx.dto.ShippingOptionDTO standard = new com.shopx.dto.ShippingOptionDTO();
        standard.setMethod("STANDARD");
        standard.setName("标准配送");
        standard.setFee(calculateShippingFee("STANDARD", shippingAddress, BigDecimal.ZERO));
        standard.setEstimatedDays(4);
        standard.setDescription("3-5个工作日送达");
        options.add(standard);
        
        // 加急配送
        com.shopx.dto.ShippingOptionDTO express = new com.shopx.dto.ShippingOptionDTO();
        express.setMethod("EXPRESS");
        express.setName("加急配送");
        express.setFee(calculateShippingFee("EXPRESS", shippingAddress, BigDecimal.ZERO));
        express.setEstimatedDays(1);
        express.setDescription("1-2个工作日送达");
        options.add(express);
        
        // 自提
        com.shopx.dto.ShippingOptionDTO pickup = new com.shopx.dto.ShippingOptionDTO();
        pickup.setMethod("PICKUP");
        pickup.setName("门店自提");
        pickup.setFee(BigDecimal.ZERO);
        pickup.setEstimatedDays(0);
        pickup.setDescription("到店自提，免费");
        options.add(pickup);
        
        return options;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ShippingAddress addShippingAddress(Long userId, ShippingAddress address) {
        address.setUserId(userId);
        
        // 如果设置为默认，取消其他默认地址
        if (Boolean.TRUE.equals(address.getIsDefault())) {
            QueryWrapper<ShippingAddress> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("user_id", userId)
                       .eq("is_default", true);
            List<ShippingAddress> defaultAddresses = addressMapper.selectList(queryWrapper);
            for (ShippingAddress addr : defaultAddresses) {
                addr.setIsDefault(false);
                addressMapper.updateById(addr);
            }
        }
        
        addressMapper.insert(address);
        return address;
    }
    
    @Override
    public List<ShippingAddress> getUserShippingAddresses(Long userId) {
        QueryWrapper<ShippingAddress> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId)
                   .orderByDesc("is_default")
                   .orderByDesc("create_time");
        return addressMapper.selectList(queryWrapper);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean setDefaultAddress(Long addressId, Long userId) {
        ShippingAddress address = addressMapper.selectById(addressId);
        if (address == null || !address.getUserId().equals(userId)) {
            return false;
        }
        
        // 取消其他默认地址
        QueryWrapper<ShippingAddress> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId)
                   .eq("is_default", true);
        List<ShippingAddress> defaultAddresses = addressMapper.selectList(queryWrapper);
        for (ShippingAddress addr : defaultAddresses) {
            addr.setIsDefault(false);
            addressMapper.updateById(addr);
        }
        
        // 设置新的默认地址
        address.setIsDefault(true);
        addressMapper.updateById(address);
        return true;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteShippingAddress(Long addressId, Long userId) {
        ShippingAddress address = addressMapper.selectById(addressId);
        if (address == null || !address.getUserId().equals(userId)) {
            return false;
        }
        
        addressMapper.deleteById(addressId);
        return true;
    }
    
    private int getStatusIndex(String status) {
        String[] statuses = {"PENDING", "IN_TRANSIT", "OUT_FOR_DELIVERY", "DELIVERED"};
        for (int i = 0; i < statuses.length; i++) {
            if (statuses[i].equals(status)) {
                return i;
            }
        }
        return 0;
    }
    
    private String convertToJson(Map<String, Object> map) {
        // 简单的JSON转换
        StringBuilder json = new StringBuilder("{");
        boolean first = true;
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            if (!first) {
                json.append(",");
            }
            json.append("\"").append(entry.getKey()).append("\":\"")
                .append(entry.getValue()).append("\"");
            first = false;
        }
        json.append("}");
        return json.toString();
    }
}

