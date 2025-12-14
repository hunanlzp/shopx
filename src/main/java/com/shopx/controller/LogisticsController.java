package com.shopx.controller;

import com.shopx.annotation.ApiVersion;
import com.shopx.entity.ApiResponse;
import com.shopx.entity.LogisticsTracking;
import com.shopx.entity.ShippingAddress;
import com.shopx.service.LogisticsService;
import com.shopx.util.ResponseUtil;
import com.shopx.util.SaTokenUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 物流控制器
 */
@Slf4j
@RestController
@RequestMapping("/logistics")
@ApiVersion("v1")
@Tag(name = "物流管理", description = "物流追踪和配送相关API")
public class LogisticsController {
    
    @Autowired
    private LogisticsService logisticsService;
    
    /**
     * 获取物流追踪信息
     */
    @Operation(summary = "获取物流追踪信息", description = "根据订单ID获取物流追踪信息")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "查询成功")
    })
    @GetMapping("/tracking/{orderId}")
    public ResponseEntity<ApiResponse<LogisticsTracking>> getTrackingInfo(
            @Parameter(description = "订单ID", required = true) @PathVariable Long orderId) {
        
        try {
            LogisticsTracking tracking = logisticsService.getTrackingInfo(orderId);
            if (tracking == null) {
                return ResponseUtil.error("未找到物流信息");
            }
            return ResponseUtil.success("查询成功", tracking);
        } catch (Exception e) {
            log.error("获取物流追踪信息失败", e);
            return ResponseUtil.error("获取物流追踪信息失败，请稍后重试");
        }
    }
    
    /**
     * 同步物流信息
     */
    @Operation(summary = "同步物流信息", description = "从第三方API同步物流信息")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "同步成功")
    })
    @PostMapping("/sync")
    public ResponseEntity<ApiResponse<LogisticsTracking>> syncLogisticsInfo(
            @Parameter(description = "物流单号", required = true) @RequestParam String trackingNumber,
            @Parameter(description = "物流公司", required = true) @RequestParam String logisticsCompany) {
        
        try {
            LogisticsTracking tracking = logisticsService.syncLogisticsInfo(trackingNumber, logisticsCompany);
            if (tracking == null) {
                return ResponseUtil.error("未找到物流信息");
            }
            return ResponseUtil.success("同步成功", tracking);
        } catch (Exception e) {
            log.error("同步物流信息失败", e);
            return ResponseUtil.error("同步物流信息失败，请稍后重试");
        }
    }
    
    /**
     * 计算配送费用
     */
    @Operation(summary = "计算配送费用", description = "根据配送方式和地址计算配送费用")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "计算成功")
    })
    @GetMapping("/shipping-fee")
    public ResponseEntity<ApiResponse<BigDecimal>> calculateShippingFee(
            @Parameter(description = "配送方式", required = true) @RequestParam String shippingMethod,
            @Parameter(description = "收货地址", required = true) @RequestParam String shippingAddress,
            @Parameter(description = "订单总金额", required = false) @RequestParam(required = false) BigDecimal totalAmount) {
        
        try {
            BigDecimal fee = logisticsService.calculateShippingFee(
                    shippingMethod, shippingAddress, totalAmount != null ? totalAmount : BigDecimal.ZERO);
            return ResponseUtil.success("计算成功", fee);
        } catch (Exception e) {
            log.error("计算配送费用失败", e);
            return ResponseUtil.error("计算配送费用失败，请稍后重试");
        }
    }
    
    /**
     * 获取配送选项
     */
    @Operation(summary = "获取配送选项", description = "获取可用的配送方式和费用")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "查询成功")
    })
    @GetMapping("/shipping-options")
    public ResponseEntity<ApiResponse<List<com.shopx.dto.ShippingOptionDTO>>> getShippingOptions(
            @Parameter(description = "收货地址", required = true) @RequestParam String shippingAddress) {
        
        try {
            List<com.shopx.dto.ShippingOptionDTO> options = logisticsService.getShippingOptions(shippingAddress);
            return ResponseUtil.success("查询成功", options);
        } catch (Exception e) {
            log.error("获取配送选项失败", e);
            return ResponseUtil.error("获取配送选项失败，请稍后重试");
        }
    }
    
    /**
     * 添加收货地址
     */
    @Operation(summary = "添加收货地址", description = "添加新的收货地址")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "添加成功")
    })
    @PostMapping("/address")
    public ResponseEntity<ApiResponse<ShippingAddress>> addShippingAddress(
            @Parameter(description = "收货地址信息", required = true) @RequestBody ShippingAddress address) {
        
        try {
            Long userId = SaTokenUtil.getCurrentUserId();
            ShippingAddress savedAddress = logisticsService.addShippingAddress(userId, address);
            return ResponseUtil.success("添加成功", savedAddress);
        } catch (Exception e) {
            log.error("添加收货地址失败", e);
            return ResponseUtil.error("添加收货地址失败，请稍后重试");
        }
    }
    
    /**
     * 获取用户收货地址列表
     */
    @Operation(summary = "获取收货地址列表", description = "获取当前用户的所有收货地址")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "查询成功")
    })
    @GetMapping("/address")
    public ResponseEntity<ApiResponse<List<ShippingAddress>>> getUserShippingAddresses() {
        
        try {
            Long userId = SaTokenUtil.getCurrentUserId();
            List<ShippingAddress> addresses = logisticsService.getUserShippingAddresses(userId);
            return ResponseUtil.success("查询成功", addresses);
        } catch (Exception e) {
            log.error("获取收货地址列表失败", e);
            return ResponseUtil.error("获取收货地址列表失败，请稍后重试");
        }
    }
    
    /**
     * 设置默认地址
     */
    @Operation(summary = "设置默认地址", description = "设置指定的收货地址为默认地址")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "设置成功")
    })
    @PutMapping("/address/{addressId}/default")
    public ResponseEntity<ApiResponse<Void>> setDefaultAddress(
            @Parameter(description = "地址ID", required = true) @PathVariable Long addressId) {
        
        try {
            Long userId = SaTokenUtil.getCurrentUserId();
            boolean success = logisticsService.setDefaultAddress(addressId, userId);
            if (success) {
                return ResponseUtil.success("设置成功", null);
            } else {
                return ResponseUtil.error("设置失败，地址不存在或无权限");
            }
        } catch (Exception e) {
            log.error("设置默认地址失败", e);
            return ResponseUtil.error("设置默认地址失败，请稍后重试");
        }
    }
    
    /**
     * 删除收货地址
     */
    @Operation(summary = "删除收货地址", description = "删除指定的收货地址")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "删除成功")
    })
    @DeleteMapping("/address/{addressId}")
    public ResponseEntity<ApiResponse<Void>> deleteShippingAddress(
            @Parameter(description = "地址ID", required = true) @PathVariable Long addressId) {
        
        try {
            Long userId = SaTokenUtil.getCurrentUserId();
            boolean success = logisticsService.deleteShippingAddress(addressId, userId);
            if (success) {
                return ResponseUtil.success("删除成功", null);
            } else {
                return ResponseUtil.error("删除失败，地址不存在或无权限");
            }
        } catch (Exception e) {
            log.error("删除收货地址失败", e);
            return ResponseUtil.error("删除收货地址失败，请稍后重试");
        }
    }
}

